package com.example.demo.topology;

import com.example.demo.avro.CommandEvent;
import com.example.demo.config.SerdesFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Component
@EnableKafkaStreams
@Slf4j
public class CommandsConsumer {

    public static final String DB_STATUS = "db-status";
    private final String commandsTopic;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    public CommandsConsumer(@Value("${topics.commands.name:commands}") String commandsTopic) {
        this.commandsTopic = commandsTopic;
    }

    @Bean("commandEventGlobalKTable")
    public GlobalKTable<String, CommandEvent> commandEventGlobalKTable(StreamsBuilder builder) {
        return builder.globalTable(commandsTopic, Consumed.with(Serdes.String(), SerdesFactory.commandEventSerde()), Materialized.as(commandsTopic + "-store"));
    }


    @KafkaListener(
            topics = "${topics.commands.name:commands}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleMessage(ConsumerRecord<String, CommandEvent> commandEvent) {
        log.info(String.format("[CommandEvent] event consumed -> %s", commandEvent.value()));
        handleCommandEvent(commandEvent.value());
    }

    @Bean
    public StreamsBuilderFactoryBeanCustomizer streamsBuilderFactoryBeanCustomizer(StreamsBuilderFactoryBean factoryBean) {
        return sfb -> sfb.setStateListener((newState, oldState) -> {
            if (KafkaStreams.State.RUNNING == newState) {
                log.info("Streams are now in a running state....");
                KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
                ReadOnlyKeyValueStore<String, CommandEvent> commandEventReadOnlyKeyValueStore = kafkaStreams.store(
                        StoreQueryParameters.fromNameAndType(commandsTopic + "-store", QueryableStoreTypes.keyValueStore())
                );
                KeyValueIterator<String, CommandEvent> commandEvents = commandEventReadOnlyKeyValueStore.all();
                boolean dbStatusProcessed = false;
                while (commandEvents.hasNext()) {
                    KeyValue<String, CommandEvent> commandEventKeyValue = commandEvents.next();
                    handleCommandEvent(commandEventKeyValue.value);
                    dbStatusProcessed = commandEventKeyValue.key.equalsIgnoreCase(DB_STATUS) || dbStatusProcessed;
                }
                if(!dbStatusProcessed) {
                    log.info("[db-status] No status set - starting listener");
                    kafkaListenerEndpointRegistry.getListenerContainer("payloads-listener-id").start();
                }

            }
        });
    }

    private void handleCommandEvent(CommandEvent commandEvent) {

        if (null != commandEvent) {
            //switch is to support other 'control' events
            switch (commandEvent.getCommandId()) {
                case DB_STATUS:
                    handleDbStatusEvent(commandEvent.getCommand());
            }
        }
    }

    private void handleDbStatusEvent(String command) {
        if ("started".equalsIgnoreCase(command)) {
            log.info("[db-status command event] starting listener");
            kafkaListenerEndpointRegistry.getListenerContainer("payloads-listener-id").start();
        } else {
            log.info("[db-status command event] stopping listener(if not already stopped)");
            kafkaListenerEndpointRegistry.getListenerContainer("payloads-listener-id").stop();
        }
    }
}