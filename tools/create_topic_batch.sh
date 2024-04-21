#!/bin/bash
topics=(
#experience_insights.session_summaries.PT1M.13
tlb-rt-ad-sess-summary-nbcu-d2c
)

BOOTSTRAP_SERVERS="rccp404-27a.iad6.prod.conviva.com:31911"
KAFKA_HOME="/Users/rwei/Downloads/kafka_2.12-3.1.2"
#PARTITIONS=300
#REPLICATION_FACTOR=2

for TOPIC in "${topics[@]}"; do
#  bash -x "$KAFKA_HOME"/bin/kafka-topics.sh --describe --bootstrap-server $BOOTSTRAP_SERVERS --topic $TOPIC --partitions $PARTITIONS --replication-factor $REPLICATION_FACTOR
  bash -x "$KAFKA_HOME"/bin/kafka-configs.sh --alter --bootstrap-server $BOOTSTRAP_SERVERS --entity-type topics --entity-name $TOPIC --add-config 'compression.type=gzip,segment.bytes=536870912,retention.ms=2400000,retention.bytes=2147483648'
done

echo "===========================checking created topics============================"

for TOPIC in "${topics[@]}"; do
  bash -x "$KAFKA_HOME"/bin/kafka-topics.sh --describe --bootstrap-server $BOOTSTRAP_SERVERS --topic $TOPIC
done
