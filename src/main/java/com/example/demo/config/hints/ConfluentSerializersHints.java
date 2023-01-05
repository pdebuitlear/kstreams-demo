package com.example.demo.config.hints;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.util.stream.Stream;

public class ConfluentSerializersHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ReflectionHints reflectionHints = hints.reflection();

        Stream.of(
                        org.apache.commons.compress.archivers.ArchiveOutputStream.class,
                        io.confluent.kafka.serializers.KafkaAvroSerializer.class,
                        io.confluent.kafka.serializers.KafkaAvroDeserializer.class,
                        io.confluent.kafka.serializers.context.NullContextNameStrategy.class,
                        io.confluent.kafka.serializers.subject.DefaultReferenceSubjectNameStrategy.class,
                        io.confluent.kafka.serializers.subject.RecordNameStrategy.class,
                        io.confluent.kafka.serializers.subject.TopicNameStrategy.class,
                        io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class,
                        io.confluent.kafka.serializers.subject.strategy.SubjectNameStrategy.class,
                        io.confluent.kafka.serializers.subject.strategy.ReferenceSubjectNameStrategy.class,

                        io.confluent.kafka.streams.serdes.avro.GenericAvroSerde.class,
                        io.confluent.kafka.streams.serdes.avro.PrimitiveAvroSerde.class,
                        io.confluent.kafka.streams.serdes.avro.ReflectionAvroSerde.class,
                        io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde.class,

                        io.confluent.kafka.streams.serdes.avro.GenericAvroDeserializer.class,
                        io.confluent.kafka.streams.serdes.avro.GenericAvroSerializer.class,
                        io.confluent.kafka.streams.serdes.avro.ReflectionAvroDeserializer.class,
                        io.confluent.kafka.streams.serdes.avro.ReflectionAvroSerializer.class,
                        io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer.class,
                        io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer.class
                )
                .forEach(type -> reflectionHints.registerType(type, builder ->
                        builder.withMembers(
                                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
                        )));

    }

}