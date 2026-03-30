package com.wave.notification_service.services;

import java.security.KeyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.stereotype.Service;

import com.wave.notification_service.dtos.UserState;

import reactor.core.publisher.Mono;

@Service
public class UserStateService {
    private final ReactiveValueOperations<String, UserState> valueOps;

    public UserStateService(
        @Qualifier("userStateStorageOperations")
        ReactiveRedisOperations<String, UserState> ops) {
        valueOps = ops.opsForValue();
    }

    public Mono<Boolean> set(String key, UserState value) {
        return valueOps.set(key, value);
    }

    public Mono<UserState> getByKey(String key) {
        return valueOps.get(key)
            .switchIfEmpty(Mono.error(new KeyException("Key not found")));
    }

    public Mono<Void> deleteByKey(String key) {
        return valueOps.delete(key)
            .flatMap(success -> success ?
                Mono.empty() :
                Mono.error(new ValidationFailureException("Bad key")));
    }
}

