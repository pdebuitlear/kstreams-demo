package com.example.demo.config.hints;

import org.springframework.aot.hint.*;

import java.util.stream.Stream;

public class KafkaStreamsHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ReflectionHints reflectionHints = hints.reflection();

        TypeReference stateDirectoryProcessFile = TypeReference.of("org.apache.kafka.streams.processor.internals.StateDirectory$StateDirectoryProcessFile");
        reflectionHints.registerType(stateDirectoryProcessFile,
                builder -> builder.withMembers(
                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
                ));

        ReflectionHints jniHints = hints.jni();
        Stream.of(
                        org.rocksdb.RocksDBException.class,
                        org.rocksdb.Status.class
                )
                .forEach(type -> jniHints.registerType(type, builder ->
                        builder.withMembers(
                                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                                MemberCategory.INVOKE_DECLARED_METHODS,
                                MemberCategory.INVOKE_PUBLIC_METHODS,
                                MemberCategory.DECLARED_FIELDS,
                                MemberCategory.PUBLIC_FIELDS
                        )));

        hints.resources().registerPattern("librocksdbjni-linux*");
        hints.resources().registerPattern("kafka/kafka-version.properties");
        hints.resources().registerPattern("*/kafka-streams-version.properties");

    }

}