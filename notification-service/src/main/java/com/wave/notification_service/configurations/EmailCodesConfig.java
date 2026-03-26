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

import com.wave.notification_service.dtos.EmailCodes;

import lombok.Data;

@Configuration
public class EmailCodesConfig {
    @Bean("emailCodesProperties")
    @ConfigurationProperties(prefix = "spring.redis.email-codes")
    public RedisProperties emailCodesProperties() {
        return new RedisProperties();
    }

    @Bean("emailCodesFactory")
    public ReactiveRedisConnectionFactory emailCodesConnectionFactory(
        @Qualifier("emailCodesProperties") RedisProperties props) {
        return new LettuceConnectionFactory(props.getHost(), props.getPort());
    }

    @Bean("emailCodesOperations")
    public ReactiveRedisOperations<String, EmailCodes> emailCodesOperations(
        @Qualifier("emailCodesFactory") ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        JacksonJsonRedisSerializer<EmailCodes> valueSerializer =
                new JacksonJsonRedisSerializer<>(EmailCodes.class);

        RedisSerializationContext<String, EmailCodes> context =
                RedisSerializationContext.<String, EmailCodes>newSerializationContext(keySerializer)
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
