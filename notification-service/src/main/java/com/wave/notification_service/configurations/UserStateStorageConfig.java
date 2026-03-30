package com.wave.notification_service.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.wave.notification_service.dtos.UserState;

import lombok.Data;

@Configuration
public class UserStateStorageConfig {
    @Bean("userStateStorageProperties")
    @ConfigurationProperties(prefix = "spring.redis.user-state")
    public RedisProperties userStateStorageProperties() {
        return new RedisProperties();
    }

    @Bean("userStateStorageFactory")
    public ReactiveRedisConnectionFactory userStateStorageConnectionFactory(
        @Qualifier("userStateStorageProperties") RedisProperties props) {
        return new LettuceConnectionFactory(props.getHost(), props.getPort());
    }

    @Bean("userStateStorageOperations")
    public ReactiveRedisOperations<String, UserState> userStateStorageOperations(
        @Qualifier("userStateStorageFactory") ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        JacksonJsonRedisSerializer<UserState> valueSerializer =
                new JacksonJsonRedisSerializer<>(UserState.class);

        RedisSerializationContext<String, UserState> context =
                RedisSerializationContext.<String, UserState>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Data
    public static class RedisProperties {
        private String host;
        private int port;
    }
}

