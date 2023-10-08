#!/bin/bash

KAFKA_HOME=/opt/kafka
KAFKA_BROKER=node1:9092,node2:9092,node3:9092
KAFKA_TOPIC=druid_ingestion

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