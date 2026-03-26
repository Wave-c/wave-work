package com.wave.notification_service.components.senders;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.wave.notification_service.models.NotificationChannel;
import com.wave.notification_service.models.NotificationChannelType;
import com.wave.notification_service.services.BotService;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Log4j2
@Order(1)
@Component
@RequiredArgsConstructor
public class TelegramSender implements INotificationSender {
    private final Unleash unleash;
    private final BotService botService;

    @Override
    public Boolean supports(UnleashContext context) {
        return unleash.isEnabled("send-to-telegram", context);
    }

    @Override
    public Mono<Void> send(NotificationChannel channel, String message) {
        log.info("Sending to telegram");
        return Mono.fromCallable(() -> {
            SendMessage send = SendMessage.builder()
                .chatId(channel.getValue())
                .text(message)
                .parseMode(ParseMode.HTML)
                .build();
            return botService.execute(send);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.TELEGRAM;
    }
}