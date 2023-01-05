package com.example.demo.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Component
@ConfigurationProperties
public class TopicConfig {

    private Map<String, Topic> topics = new HashMap<>();

    @Getter
    @Setter
    public static class Topic {
        private String name;
        private int partitionsNum;
        private int replicationFactor;

        @Override
        public String toString() {
            return "Menu{" +
                    "name='" + this.name + '\'' +
                    ", partitionsNum='" + this.partitionsNum + '\'' +
                    ", replicationFactor='" + this.replicationFactor + '\'' +
                    '}';
        }
    }

}
