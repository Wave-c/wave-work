package com.wave.notification_service.services;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class SingleUseLinkService {
    private final ReactiveValueOperations<String, String> valueOps;

    public SingleUseLinkService(
        // @Qualifier("singleUseLinkRedisOperations")
        ReactiveRedisOperations<String, String> ops) {
        valueOps = ops.opsForValue();
    }

    public Mono<Boolean> setIfAbsent(String key, String value) {
        return valueOps.setIfAbsent(key, value, Duration.ofDays(1));
    }

    public Mono<String> getByKey(String key) {
        return valueOps.get(key);
    }

    public Mono<Void> deleteByKey(String key) {
        return valueOps.delete(key)
            .flatMap(success -> success ?
                Mono.empty() :
                Mono.error(new ValidationFailureException("Bad auth token")));
    }

    public Mono<String> getNew(UUID userId) {
        String token = UUID.randomUUID().toString();

        return setIfAbsent(token, userId.toString())
            .thenReturn(token);
    }
}
