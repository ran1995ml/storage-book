package com.ran.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * ConsumerDemo
 *
 * @author rwei
 * @since 2023/12/5 17:42
 */
public class ConsumerDemo {
    public static void main(String[] args) {
        String bootstrapServers = "rccp213-9c.iad4.prod.conviva.com:9092,rccp213-9d.iad4.prod.conviva.com:9092,rccp214-9c.iad4.prod.conviva.com:9092";
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            String topic = "experience_insights.session_summaries.PT1M.1";

//            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
            List<TopicPartition> list = new ArrayList<>();
            for (int i=0;i<9;i++) {
                TopicPartition topicPartition = new TopicPartition(topic, i);
                list.add(topicPartition);
            }

            consumer.assign(list);
            consumer.seekToEnd(list);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                System.out.println(records.count());
            }
        }

    }
}
