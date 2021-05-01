package com.microservices.demo.elastic.query.service.config;

import com.microservices.demo.config.ElasticQueryServiceConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private final ElasticQueryServiceConfigData.WebClient elasticQueryServiceConfigData;

    public WebClientConfig(ElasticQueryServiceConfigData queryServiceConfigData) {
        this.elasticQueryServiceConfigData = queryServiceConfigData.getWebClient();
    }

    @LoadBalanced
    @Bean
    WebClient.Builder webClientBuilder() {

        // Here we do not configure OAuth2 onto the web client (i.e. this service as a web client to KafkaStreamsService)
        // rather we pass the access token manually through the header of each request, at TwitterElasticQueryService.retrieveResponseModel()
        // See the webClientConfig of elastic-query-web-client for the other way of using OAuth...

        return WebClient.builder()
                // Here we do not set the baseURL for the webclient,
                // because this client (query service) will be the client for multiple services: KafkaStreamsService and analytics service
                // Base URI is set instead at TwitterElasticQueryService.retrieveResponseModel()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, elasticQueryServiceConfigData.getContentType())
                .defaultHeader(HttpHeaders.ACCEPT, elasticQueryServiceConfigData.getAcceptType())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer
                                .defaultCodecs()
                                .maxInMemorySize(elasticQueryServiceConfigData.getMaxInMemorySize()));
    }

    // A private TCP client to customize the TCP time out values
    private TcpClient getTcpClient() {
        return TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryServiceConfigData.getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(elasticQueryServiceConfigData.getReadTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(elasticQueryServiceConfigData.getWriteTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                });
    }
}
