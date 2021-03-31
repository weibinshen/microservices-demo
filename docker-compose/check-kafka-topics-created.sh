#!/bin/bash
# check-kafka-topics-created.sh

# We need to make sure we run 'chmod +x check-kafka-topics-created.sh'
# After the service is running, its console should log the doc ID.
# then we can query it from Postman: e.g. localhost:9200/twitter-index/_search?q=id:1376017922397630464

apt-get update -y

yes | apt-get install kafkacat

kafkacatResult=$(kafkacat -L -b kafka-broker-1:9092)

echo "kafkacat result:" $kafkacatResult

while [[ ! $kafkacatResult == *"twitter-topic"* ]]; do
  >&2 echo "Kafka topic has not been created yet!"
  sleep 2
  kafkacatResult=$(kafkacat -L -b kafka-broker-1:9092)
done

./cnb/lifecycle/launcher