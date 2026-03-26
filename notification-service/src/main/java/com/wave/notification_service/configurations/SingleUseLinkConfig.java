package com.wave.notification_service.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import lombok.Data;

@Configuration
public class SingleUseLinkConfig {
    @Primary
    @Bean("singleUseLinkProperties")
    @ConfigurationProperties(prefix = "spring.redis.single-use-link")
    public RedisProperties singleUseLinkProperties() {
        return new RedisProperties();
    }

    @Primary
    @Bean("singleUseLinkFactory")
    public ReactiveRedisConnectionFactory singleUseLinkConnectionFactory(
        @Qualifier("singleUseLinkProperties") RedisProperties props) {
        return new LettuceConnectionFactory(props.getHost(), props.getPort());
    }

    @Primary
    @Bean("singleUseLinkOperations")
    public ReactiveRedisOperations<String, String> singleUseLinkOperations(
        @Qualifier("singleUseLinkFactory") ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }

    @Data
    public static class RedisProperties {
        private String host;
        private int port;
    }
}
