#!/bin/bash

KAFKA_HOME=/Users/rwei/Downloads/kafka_2.12-3.1.2
KAFKA_BROKER=rccp109-5d.iad4.prod.conviva.com:30200
KAFKA_TOPIC=test1

mock_json() {
  TIMESTAMP=$(date +%s)
  VALUE1=$((RANDOM % 100))
  VALUE2=$((RANDOM % 100))
  VALUE3=$((RANDOM % 100))
  echo "{\"timestamp\": $TIMESTAMP, \"value1\": $VALUE1, \"value2\": $VALUE2, \"value3\": $VALUE3}"
}

while true; do
  DATA=$(mock_json)
  echo "Sending message : $DATA"
  echo $DATA | $KAFKA_HOME/bin/kafka-console-producer.sh --broker-list $KAFKA_BROKER --topic $KAFKA_TOPIC
  sleep 5
done
