package com.example.demo.topology;

import com.example.demo.avro.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Slf4j
public class PayloadsProducer {
    private final KafkaTemplate<String, Payload> payloadKafkaTemplate;
    @Value("${topics.payloads.name:payloads}")
    private String payloadsTopic;

    @Autowired
    public PayloadsProducer(KafkaTemplate<String, Payload> payloadKafkaTemplate) {
        this.payloadKafkaTemplate = payloadKafkaTemplate;
    }

    public void sendPayload(Payload Payload) {
        this.payloadKafkaTemplate.send(this.payloadsTopic, String.valueOf(Payload.getId()), Payload);
        log.info(String.format("Produced Payload -> %s", Payload));
    }

}