package com.wave.notification_service.components.senders;

import com.wave.notification_service.dtos.User;

import io.getunleash.UnleashContext;
import reactor.core.publisher.Mono;

public interface INotificationSender {
    Boolean supports(UnleashContext context);
    Mono<Void> send(User user, String message);
}
