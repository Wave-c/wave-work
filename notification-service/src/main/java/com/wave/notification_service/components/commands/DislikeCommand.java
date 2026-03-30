package com.wave.notification_service.components.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.wave.notification_service.components.BotSender;
import com.wave.notification_service.components.web.RecomendationWebClient;
import com.wave.notification_service.services.UserStateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class DislikeCommand implements ICommand {
    private final UserStateService userStateService;
    private final RecomendationWebClient recomendationWebClient;
    private final BotSender botSender;

    @Override
    public Mono<Void> execute(Message message) {
        log.info("Dislike command");
        return userStateService.getByKey(message.getChatId().toString())
        .doOnError(err -> log.error(err))
        .doOnNext(userState -> log.info("UserState: {}", userState))
        .flatMap(userState -> recomendationWebClient.getNext(
                userState.getUserId(),
                userState.getNextWorkHash())
            .doOnError(err -> {
                log.error(err);
            })
            .flatMap(nextWork -> {
                log.info("Next work: {}", nextWork);

                return botSender.sendText(message.getChatId(),
                    "ℹ️ <b>%s</b>\n\n📄 Описание: \n%s".formatted(
                        nextWork.getItems().getFirst().getTitle(),
                        nextWork.getItems().getFirst().getDescription()),
                        null)
                    .then(userStateService.set(message.getChatId().toString(),
                        userState.setCurrentWorkHash(userState.getNextWorkHash())
                                 .setNextWorkHash(nextWork.getNextCursor())
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
            && update.getMessage().getText().equals("👎");
    }

}
