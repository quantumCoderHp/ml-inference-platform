package com.inference.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic imageProcessingTopic() {
        return TopicBuilder.name("image-processing-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic classificationResultsTopic() {
        return TopicBuilder.name("image-classification-results")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic processingErrorsTopic() {
        return TopicBuilder.name("image-processing-errors")
                .partitions(3)
                .replicas(1)
                .build();
    }

}