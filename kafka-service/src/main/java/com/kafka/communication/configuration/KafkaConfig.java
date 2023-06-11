package com.kafka.communication.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.kafka.communication.utils.AppConstants;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic inventoryTopic() {
        return TopicBuilder.name(AppConstants.TOPIC_NAME)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
