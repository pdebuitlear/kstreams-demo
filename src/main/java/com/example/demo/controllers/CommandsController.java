package com.example.demo.controllers;

import com.example.demo.avro.CommandEvent;
import com.example.demo.model.CommandEventReq;
import com.example.demo.topology.CommandsProducer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/commands")
public class CommandsController {

    private final CommandsProducer commandsProducer;
    private final StreamsBuilderFactoryBean factoryBean;
    private final String commandsTopic;

    @Autowired
    CommandsController(CommandsProducer commandsProducer, StreamsBuilderFactoryBean factoryBean, @Value("${topics.commands.name:commands}") String commandsTopic) {
        this.commandsProducer = commandsProducer;
        this.factoryBean = factoryBean;
        this.commandsTopic = commandsTopic;
    }

    @PostMapping
    public void sendMessageToKafkaTopic(@RequestBody CommandEventReq request) {
        CommandEvent commandEvent = CommandEvent.newBuilder().setCommandId(request.getCommandId()).setCommand(request.getCommand()).build();
        this.commandsProducer.sendCommandEvent(commandEvent);
    }

    @RequestMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommandEventReq getCommandEventFromStore(@PathVariable String id) {
        KafkaStreams kafkaStreams = this.factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, CommandEvent> counts = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(this.commandsTopic + "-store", QueryableStoreTypes.keyValueStore())
        );
        CommandEvent commandEvent = counts.get(id);
        if (null != commandEvent)
            return CommandEventReq.builder()
                    .commandId(counts.get(id).getCommandId())
                    .command(counts.get(id).getCommand()).build();
        else
            return null;
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommandEventReq> getAllCommandEventFromStore() {
        KafkaStreams kafkaStreams = this.factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, CommandEvent> counts = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(this.commandsTopic + "-store", QueryableStoreTypes.keyValueStore())
        );
        KeyValueIterator<String, CommandEvent> commandEvents = counts.all();
        List<CommandEventReq> events = new ArrayList<>();
        while (commandEvents.hasNext()) {
            CommandEvent commandEvent = commandEvents.next().value;
            events.add(CommandEventReq.builder().commandId(commandEvent.getCommandId()).command(commandEvent.getCommand()).build());
        }

        return events;
    }

}