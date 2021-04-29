package com.microservices.demo.kafka.streams.service.runner.impl;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaStreamsConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAnalyticsAvroModel;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.streams.service.runner.StreamsRunner;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Component
public class KafkaStreamsRunner implements StreamsRunner<String, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamsRunner.class);

    private static final String REGEX = "\\W+"; // Use any non-word character as delimiter in order to obtain word count

    private final KafkaStreamsConfigData kafkaStreamsConfigData;

    private final KafkaConfigData kafkaConfigData;

    private final Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;

    // volatile keyword forces the program to ignore values in CPU caches, and always fetch from main memory for each read.
    private volatile ReadOnlyKeyValueStore<String, Long> keyValueStore;

    public KafkaStreamsRunner(KafkaStreamsConfigData kafkaStreamsConfig,
                              KafkaConfigData kafkaConfig,
                              @Qualifier("streamConfiguration") Properties streamsConfiguration) {
        this.kafkaStreamsConfigData = kafkaStreamsConfig;
        this.kafkaConfigData = kafkaConfig;
        this.streamsConfiguration = streamsConfiguration;
    }

    @Override
    public void start() {
        final Map<String, String> serdeConfig = Collections.singletonMap(
                kafkaConfigData.getSchemaRegistryUrlKey(),
                kafkaConfigData.getSchemaRegistryUrl());

        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        final KStream<Long, TwitterAvroModel> twitterAvroModelKStream =
                getTwitterAvroModelKStream(serdeConfig, streamsBuilder);

        createTopology(twitterAvroModelKStream, serdeConfig);

        startStreaming(streamsBuilder);
    }

    @Override
    public Long getValueByKey(String word) {
        if (kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING) {
            if (keyValueStore == null) {
                synchronized (this) {
                    if (keyValueStore == null) {
                        // store: Get a facade wrapping the local StateStore instances with the provided storeName if the Store's type is accepted by the provided queryableStoreType.
                        // The returned object can be used to query the StateStore instances.
                        keyValueStore = kafkaStreams.store(StoreQueryParameters
                                .fromNameAndType(kafkaStreamsConfigData.getWordCountStoreName(),
                                        QueryableStoreTypes.keyValueStore()));
                    }
                }
            }
            return keyValueStore.get(word.toLowerCase());
        }
        return 0L;
    }

    @PreDestroy
    public void close() {
        if (kafkaStreams != null) {
            kafkaStreams.close();
            LOG.info("Kafka streaming closed!");
        }
    }


    private void startStreaming(StreamsBuilder streamsBuilder) {
        final Topology topology = streamsBuilder.build();
        LOG.info("Defined topology: {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
        kafkaStreams.start();
        LOG.info("Kafka streaming started..");
    }

    private void createTopology(KStream<Long, TwitterAvroModel> twitterAvroModelKStream,
                                Map<String, String> serdeConfig) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);

        Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsAvroModel = getSerdeAnalyticsModel(serdeConfig);

        twitterAvroModelKStream
                // flatMapValues: Create a new KStream by transforming the value of each record in this stream into zero or more values with the same key in the new stream.
                .flatMapValues(value -> Arrays.asList(pattern.split(value.getText().toLowerCase())))
                // groupBy: Group the records of this KStream on a new key that is selected using the provided KeyValueMapper and default serializers and deserializers.
                // Without groupBy to derive a KGroupedStream, we cannot perform aggregation.
                // The KeyValueMapper selects a new key (which should be of the same type) while preserving the original values.
                .groupBy((key, word) -> word)
                // count: derived the count for each aggregation key (which is the word) and saves them in a local KTable changelog stream
                // The result is written into a local KeyValueStore (which is basically an ever-updating materialized view) provided by the given store name in materialized.
                .count(Materialized
                        .<String, Long, KeyValueStore<Bytes, byte[]>>as(kafkaStreamsConfigData.getWordCountStoreName()))
                .toStream()
                // map: Transform each record of the input stream into a new record in the output stream (both key and value type can be altered arbitrarily).
                .map(mapToAnalyticsModel())
                // to: Materialize this stream to a topic using the provided Produced instance
                .to(kafkaStreamsConfigData.getOutputTopicName(),
                        // with: Create a Produced instance with provided keySerde and valueSerde.
                        Produced.with(Serdes.String(), serdeTwitterAnalyticsAvroModel));

    }

    private KeyValueMapper<String, Long, KeyValue<? extends String, ? extends TwitterAnalyticsAvroModel>>
    mapToAnalyticsModel() {
        return (word, count) -> {
            LOG.info("Sending to topic {}, word {} - count {}",
                    kafkaStreamsConfigData.getOutputTopicName(), word, count);
            return new KeyValue<>(word, TwitterAnalyticsAvroModel
                    .newBuilder()
                    .setWord(word)
                    .setWordCount(count)
                    .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build());
        };
    }

    private Serde<TwitterAnalyticsAvroModel> getSerdeAnalyticsModel(Map<String, String> serdeConfig) {
        Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAnalyticsAvroModel.configure(serdeConfig, false);
        return serdeTwitterAnalyticsAvroModel;
    }

    private KStream<Long, TwitterAvroModel> getTwitterAvroModelKStream(Map<String, String> serdeConfig,
                                                                       StreamsBuilder streamsBuilder) {
        final Serde<TwitterAvroModel> serdeTwitterAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAvroModel.configure(serdeConfig, false);
        return streamsBuilder.stream(kafkaStreamsConfigData.getInputTopicName(), Consumed.with(Serdes.Long(),
                serdeTwitterAvroModel));
    }


}
