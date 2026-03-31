package com.wave.notification_service.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wave.notification_service.components.web.ProfileWebClient;
import com.wave.notification_service.models.NotificationChannel;
import com.wave.notification_service.models.NotificationChannelType;
import com.wave.notification_service.repositories.NotificationChannelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {
    private final NotificationChannelRepository notificationChannelRepository;
    private final ProfileWebClient profileWebClient;

    public Flux<NotificationChannel> getChannels(UUID userId) {
        return notificationChannelRepository.getByUserId(userId);
    }

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
        )
        .then();
    }

    public Mono<Void> setEmail(UUID userId, String email) {
        return notificationChannelRepository.save(
            NotificationChannel.builder()
                .userId(userId)
                .value(email)
                .type(NotificationChannelType.EMAIL)
                .verified(true)
                .id(UUID.randomUUID())
                .build()
        ).then(profileWebClient.setEmail(userId, email));
    }
}
