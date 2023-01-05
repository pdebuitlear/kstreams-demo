package com.example.demo.topology;

import com.example.demo.avro.Payload;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
@Slf4j
public class PayloadsConsumer {

    @KafkaListener(id = "payloads-listener-id", autoStartup = "false",
            topics = "${topics.payloads.name:payloads}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleMessage(ConsumerRecord<String, Payload> payloadEvent) {
        log.info(String.format("[Payload] event consumed -> %s", payloadEvent.value()));
    }



}
