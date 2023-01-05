package com.example.demo.config;

import com.example.demo.avro.CommandEvent;
import com.example.demo.config.hints.ConfluentSchemaRegistryHints;
import com.example.demo.config.hints.ConfluentSerializersHints;
import com.example.demo.config.hints.KafkaStreamsHints;
import com.example.demo.config.properties.TopicConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@ImportRuntimeHints({ConfluentSchemaRegistryHints.class, ConfluentSerializersHints.class, KafkaStreamsHints.class})
public class KafkaConsumerConfig {

    @Value("${spring.kafka.properties.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistry;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;


    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", this.schemaRegistry);
        props.put("specific.avro.reader", "true");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable auto commit of offsets
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // disable auto commit of offsets
        return props;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        KafkaAvroDeserializer avroDeser = new KafkaAvroDeserializer();
        avroDeser.configure(this.consumerConfigs(), false);
        return new DefaultKafkaConsumerFactory<>(this.consumerConfigs(),
                new StringDeserializer(),
                avroDeser);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CommandEvent> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, CommandEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(this.consumerFactory());
        return factory;
    }

    @Bean
    public NewTopic commandTopic(TopicConfig topicConfig) {
        TopicConfig.Topic commandTopic = topicConfig.getTopics().get("commands");
        return TopicBuilder.name(commandTopic.getName())
                .partitions(commandTopic.getPartitionsNum())
                .replicas(commandTopic.getReplicationFactor())
                .compact()
                .build();
    }

    @Bean
    public NewTopic payloadsTopic(TopicConfig topicConfig) {
        TopicConfig.Topic payloadsTopic = topicConfig.getTopics().get("payloads");
        return TopicBuilder.name(payloadsTopic.getName())
                .partitions(payloadsTopic.getPartitionsNum())
                .replicas(payloadsTopic.getReplicationFactor())
                .compact()
                .build();
    }


}