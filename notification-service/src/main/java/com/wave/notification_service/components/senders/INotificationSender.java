package com.wave.notification_service.components.senders;

import com.wave.notification_service.models.NotificationChannel;
import com.wave.notification_service.models.NotificationChannelType;

import io.getunleash.UnleashContext;
import reactor.core.publisher.Mono;

public interface INotificationSender {
    Boolean supports(UnleashContext context);
    NotificationChannelType getChannelType();
    Mono<Void> send(NotificationChannel channel, String message);
}
