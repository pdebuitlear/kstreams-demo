package com.example.demo.config.hints;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.util.stream.Stream;

public class ConfluentSchemaRegistryHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ReflectionHints reflectionHints = hints.reflection();

        Stream.of(
                        io.confluent.kafka.schemaregistry.client.rest.entities.ErrorMessage.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.Schema.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.Config.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.SchemaTypeConverter.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.ServerClusterId.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.SubjectVersion.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.requests.CompatibilityCheckResponse.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.requests.ConfigUpdateRequest.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.requests.ModeUpdateRequest.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.requests.RegisterSchemaRequest.class,
                        io.confluent.kafka.schemaregistry.client.rest.entities.requests.RegisterSchemaResponse.class,
                        io.confluent.kafka.schemaregistry.client.security.basicauth.BasicAuthCredentialProvider.class,
                        io.confluent.kafka.schemaregistry.client.security.basicauth.BasicAuthCredentialProvider.class,
                        io.confluent.kafka.schemaregistry.client.security.basicauth.SaslBasicAuthCredentialProvider.class,
                        io.confluent.kafka.schemaregistry.client.security.basicauth.UrlBasicAuthCredentialProvider.class,
                        io.confluent.kafka.schemaregistry.client.security.basicauth.UserInfoCredentialProvider.class
                )
                .forEach(type -> reflectionHints.registerType(type, builder ->
                        builder.withMembers(
                                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                                MemberCategory.INVOKE_PUBLIC_METHODS
                        )));

    }

}