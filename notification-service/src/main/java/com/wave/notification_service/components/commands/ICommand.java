package com.wave.notification_service.components.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import reactor.core.publisher.Mono;

public interface ICommand {
    Mono<Void> execute(Message message);
    Mono<Void> execute(CallbackQuery callbackQuery);
    Boolean canExecute(Update update);
}
