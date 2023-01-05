package com.example.demo.config;

import com.example.demo.avro.CommandEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
public class SerdesFactory {

    private static Map<String, String> serdeConfig;

    public static Serde<CommandEvent> commandEventSerde() {
        Serde<CommandEvent> dataItemSerde = new SpecificAvroSerde<>();
        dataItemSerde.configure(serdeConfig, false);
        return dataItemSerde;
    }

    @Value("${spring.kafka.properties.schema.registry.url}")
    public void setSchemaRegistry(String schemaRegistry) {
        serdeConfig = Collections.singletonMap("schema.registry.url",
                schemaRegistry);
    }


}