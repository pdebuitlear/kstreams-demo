package com.example.demo.topology;

import com.example.demo.avro.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Slf4j
public class CommandsProducer {
    private final KafkaTemplate<String, CommandEvent> commandEventKafkaTemplate;
    @Value("${topics.commands.name:commands}")
    private String commandsTopic;

    @Autowired
    public CommandsProducer(KafkaTemplate<String, CommandEvent> commandEventKafkaTemplate) {
        this.commandEventKafkaTemplate = commandEventKafkaTemplate;
    }

    public void sendCommandEvent(CommandEvent commandEvent) {
        this.commandEventKafkaTemplate.send(this.commandsTopic, String.valueOf(commandEvent.getCommandId()), commandEvent);
        log.info(String.format("Produced commandEvent -> %s", commandEvent));
    }

}