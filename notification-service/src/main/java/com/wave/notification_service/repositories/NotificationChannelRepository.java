package com.wave.notification_service.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.wave.notification_service.models.NotificationChannel;
import com.wave.notification_service.models.NotificationChannelType;

import reactor.core.publisher.Mono;


public interface NotificationChannelRepository extends R2dbcRepository<NotificationChannel, UUID> {
    Mono<NotificationChannel> getByUserIdAndType(UUID userId, NotificationChannelType type);
}
