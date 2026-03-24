package com.wave.notification_service.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wave.notification_service.models.NotificationChannel;
import com.wave.notification_service.models.NotificationChannelType;
import com.wave.notification_service.repositories.NotificationChannelRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final NotificationChannelRepository notificationChannelRepository;

    public Mono<Long> getTelegramChatId(UUID userId) {
        return notificationChannelRepository.getByUserIdAndType(
            userId, NotificationChannelType.TELEGRAM)
            .map(channel -> Long.valueOf(channel.getValue()));
    }

    public Mono<Void> setTelegramChatId(UUID userId, Long telegramChatId) {
        return notificationChannelRepository.save(
            NotificationChannel.builder()
                .userId(userId)
                .value(String.valueOf(telegramChatId))
                .type(NotificationChannelType.TELEGRAM)
                .verified(true)
                .id(UUID.randomUUID())
                .build()
        ).then();
    }
}
