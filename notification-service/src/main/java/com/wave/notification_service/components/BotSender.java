package com.wave.notification_service.components;

import java.io.Serializable;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class BotSender {
    private final ObjectProvider<AbsSender> botProvider;

    public Mono<Message> sendText(
        Long chatId, String text, ReplyKeyboard keyboard) {
        SendMessage sm = SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .replyMarkup(keyboard)
            .parseMode(ParseMode.HTML)
            .build();
        return execute(sm);
    }

    private <T extends Serializable, Method extends BotApiMethod<T>> Mono<T> execute(Method method) {
        return Mono.fromCallable(() -> botProvider.getIfAvailable().execute(method))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
