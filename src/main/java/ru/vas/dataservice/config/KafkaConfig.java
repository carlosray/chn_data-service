package ru.vas.dataservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.TopicPartitionOffset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean("topicPartitions")
    public TopicPartitionOffset[] getTopicPartitions(@Qualifier("topicConfig") Map<String, List<String>> topicConfig) {
        return topicConfig.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(Integer::valueOf)
                        .map(partition -> new TopicPartitionOffset(entry.getKey(), partition)))
                .toArray(TopicPartitionOffset[]::new);
    }

    @Bean("topicConfig")
    @ConfigurationProperties(prefix = "data-service.kafka.consumer.topic-partitions")
    Map<String, List<String>> topicConfig() {
        return new HashMap<>();
    }
}
