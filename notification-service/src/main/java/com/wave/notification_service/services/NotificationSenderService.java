package com.wave.notification_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wave.notification_service.components.senders.INotificationSender;
import com.wave.notification_service.models.Notification;

import io.getunleash.UnleashContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationSenderService {
    private final List<INotificationSender> senders;

    public Mono<Void> send(Notification notification) {
        log.info("Sending notification {} to user {}", notification.getType(), notification.getRecipient().getId());

        UnleashContext context = UnleashContext.builder()
            .userId(notification.getRecipient().getId().toString())
            .addProperty("hasTelegram",
                String.valueOf(
                    notification.getRecipient().getTelegramChatId() != null))
            .build();

        return Flux.fromIterable(senders)
            .filter(sender -> sender.supports(context))
            .next()
            .switchIfEmpty(Mono.error(new RuntimeException("No sender found for notification")))
            .flatMap(sender -> sender.send(notification.getRecipient(),
                notification.getType().getMessage().formatted("some task")));
    }
}
