package com.wave.notification_service.components.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.wave.notification_service.components.BotSender;
import com.wave.notification_service.components.KeyboardFactory;
import com.wave.notification_service.components.web.RecomendationWebClient;
import com.wave.notification_service.services.UserStateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class LikeCommand implements ICommand {
    private final UserStateService userStateService;
    private final RecomendationWebClient recomendationWebClient;
    private final BotSender botSender;

    @Value("${services.frontend-service.url}")
    private String frontendServiceUrl;

    @Override
    public Mono<Void> execute(Message message) {
        log.info("Like command");

        return userStateService.getByKey(message.getChatId().toString())
        .doOnError(err -> log.error(err))
        .doOnNext(userState -> log.info("UserState: {}", userState))
        .flatMap(userState -> recomendationWebClient.getNext(
                userState.getUserId(),
                userState.getCurrentWorkHash())
            .flatMap(currentWork -> {
                log.info("Current work: {}", currentWork);

                return botSender.sendText(
                        message.getChatId(),
                        "Перейдите по ссылке👇",
                        KeyboardFactory.getTaskKeyboard(
                            "%s/task/%s".formatted(
                            frontendServiceUrl,
                            currentWork.getItems().getFirst().getId()
                        )))
                    .then(userStateService.set(message.getChatId().toString(),
                        userState.setCurrentWorkHash(userState.getNextWorkHash())
                                 .setNextWorkHash(currentWork.getNextCursor())
                    ));
            })
        )
        .then();
    }

    @Override
    public Mono<Void> execute(CallbackQuery callbackQuery) {
        return Mono.empty();
    }

    @Override
    public Boolean canExecute(Update update) {
        return update.hasMessage()
            && update.getMessage().getText().equals("👍");
    }

}
