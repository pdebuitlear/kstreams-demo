package com.example.demo.controllers;

import com.example.demo.avro.Payload;
import com.example.demo.model.PayloadReq;
import com.example.demo.topology.PayloadsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payloads")
public class PayloadsController {

    private final PayloadsProducer payloadsProducer;
    private final String payloadsTopic;

    @Autowired
    PayloadsController(PayloadsProducer payloadsProducer, StreamsBuilderFactoryBean factoryBean, @Value("${topics.payloads.name:payloads}") String payloadsTopic) {
        this.payloadsProducer = payloadsProducer;
        this.payloadsTopic = payloadsTopic;
    }

    @PostMapping
    public void sendPayloadToKafkaTopic(@RequestBody PayloadReq request) {
        Payload payload = Payload.newBuilder().setId(request.getId()).setPayload(request.getPayload()).build();
        this.payloadsProducer.sendPayload(payload);
    }

}