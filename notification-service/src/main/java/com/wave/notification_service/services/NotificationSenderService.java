package com.wave.notification_service.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wave.notification_service.components.senders.INotificationSender;
import com.wave.notification_service.models.Notification;
import com.wave.notification_service.models.NotificationChannelType;

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
    private final UserService userService;

    public Mono<Void> send(Notification notification) {
        log.info("Sending notification {} to user {}", notification.getType(), notification.getRecipient().getId());

        return userService.getChannels(notification.getRecipient().getId())
            .collectList()
            .filter(list -> !list.isEmpty())
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("No channels found for notification {} to user {}",
                    notification.getType(),
                    notification.getRecipient().getId());
                        return Mono.error(
                            new RuntimeException(
                            "No channels found for notification"));
            }))
            .flatMap(userChannels -> {
                Map<String, String> persenceMap = userChannels.stream()
                    .collect(Collectors.toMap(
                        c -> c.getType().name(),
                        c -> "true",
                        (v1, v2) -> v1)
                );

                UnleashContext context = UnleashContext.builder()
                    .userId(notification.getRecipient().getId().toString())
                    .addProperty(
                        "hasTelegram",
                        persenceMap.getOrDefault(NotificationChannelType.TELEGRAM.name(), "false"))
                    .addProperty(
                        "hasEmail",
                        persenceMap.getOrDefault(NotificationChannelType.EMAIL.name(), "false"))
                    .addProperty(
                        "hasSms",
                        persenceMap.getOrDefault(NotificationChannelType.SMS.name(), "false"))
                    .build();

                return Flux.fromIterable(senders)
                    .filter(sender -> sender.supports(context))
                    .next()
                    .flatMap(sender -> {
                        return Mono.justOrEmpty(
                            userChannels.stream()
                                .filter(ch -> ch.getType() == sender.getChannelType())
                                .findFirst()
                        )
                        .switchIfEmpty(Mono.defer(() -> {
                            log.warn("No sender found for notification {} to user {}",
                                notification.getType(),
                                notification.getRecipient().getId());
                            return Mono.error(
                                new RuntimeException(
                                    "No sender found for notification"));
                        }))
                        .flatMap(channel -> sender.send(
                            channel,
                            notification.getType().getMessage().formatted("some task")
                        ));
                });
            });
    }
}
