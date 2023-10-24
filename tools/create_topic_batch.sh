#!/bin/bash
topics=(
tlb-rt-sess-summary-nbcu
tlb-rt-sess-summary-nbcu-d2c
tlb-rt-sess-summary-bbck
tlb-rt-sess-summary-cbscom
tlb-rt-sess-summary-sling
tlb-rt-sess-summary-bskyb
tlb-rt-sess-summary-nfl
tlb-rt-sess-summary-sky
tlb-rt-sess-summary-libertyglobal
tlb-rt-sess-summary-robertscn
tlb-rt-sess-summary-mnc
tlb-rt-ad-sess-summary-nbcu
tlb-rt-ad-sess-summary-nbcu-d2c
tlb-rt-ad-sess-summary-bbck
tlb-rt-ad-sess-summary-cbscom
tlb-rt-ad-sess-summary-sling
tlb-rt-ad-sess-summary-bskyb
tlb-rt-ad-sess-summary-nfl
tlb-rt-ad-sess-summary-sky
tlb-rt-ad-sess-summary-libertyglobal
tlb-rt-ad-sess-summary-robertscn
tlb-rt-ad-sess-summary-mnc
tlb-rt-custom-sess-summary-nbcu
tlb-rt-custom-sess-summary-nbcu-d2c
tlb-rt-custom-sess-summary-bbck
tlb-rt-custom-sess-summary-cbscom
tlb-rt-custom-sess-summary-sling
tlb-rt-custom-sess-summary-bskyb
tlb-rt-custom-sess-summary-nfl
tlb-rt-custom-sess-summary-sky
tlb-rt-custom-sess-summary-libertyglobal
tlb-rt-custom-sess-summary-robertscn
tlb-rt-custom-sess-summary-mnc
)

BOOTSTRAP_SERVERS="rccp109-5d.iad4.prod.conviva.com:30200,rccp110-5c.iad4.prod.conviva.com:30200,rccp111-5b.iad4.prod.conviva.com:30200,rccp111-5d.iad4.prod.conviva.com:30200"
KAFKA_HOME="/Users/rwei/Downloads/kafka_2.12-3.1.2"
PARTITIONS=5
REPLICATION_FACTOR=3

for TOPIC in "${topics[@]}"; do
  bash -x "$KAFKA_HOME"/bin/kafka-topics.sh --create --bootstrap-server $BOOTSTRAP_SERVERS --topic $TOPIC --partitions $PARTITIONS --replication-factor $REPLICATION_FACTOR
  bash -x "$KAFKA_HOME"/bin/kafka-configs.sh --alter --bootstrap-server $BOOTSTRAP_SERVERS --entity-type topics --entity-name $TOPIC --add-config 'segment.bytes=209715200,retention.ms=1800000,retention.bytes=2147483648'
done

echo "===========================checking created topics============================"

for TOPIC in "${topics[@]}"; do
  bash -x "$KAFKA_HOME"/bin/kafka-topics.sh --describe --bootstrap-server $BOOTSTRAP_SERVERS --topic $TOPIC
done