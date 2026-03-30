package com.wave.notification_service.components.commands;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.wave.notification_service.components.BotSender;
import com.wave.notification_service.components.KeyboardFactory;
import com.wave.notification_service.components.web.ProfileWebClient;
import com.wave.notification_service.dtos.UserState;
import com.wave.notification_service.services.SingleUseLinkService;
import com.wave.notification_service.services.UserService;
import com.wave.notification_service.services.UserStateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class StartCommand implements ICommand{
    private final UserService userService;
    private final SingleUseLinkService singleUseLinkService;
    private final UserStateService userStateService;
    private final DislikeCommand dislikeCommand;
    private final BotSender botSender;
    private final ProfileWebClient profileWebClient;


    @Override
    public Mono<Void> execute(Message message) {
        String[] parts = message.getText().split(" ");
        if (parts.length < 2) {
            return botSender.sendText(
                message.getChatId(),
                "👾 Я бот для поиска задач, на бирже WaveWork. \n\nЧтобы привязать телеграм, перейдите в личный кабинет и нажмите на кнопку 'Привязать Телеграм'.",
                KeyboardFactory.getMainKeyboard())
                .then();
        }
        return singleUseLinkService.getByKey(parts[1])
            .flatMap(userId -> Mono.zip(
                userService.setTelegramChatId(
                    UUID.fromString(userId),
                    message.getChatId()),
                profileWebClient.setTelegramUsername(
                        UUID.fromString(userId),
                        message.getChat().getUserName() != null
                            ? message.getChat().getUserName()
                            : "user:" + message.getChatId()),
                userStateService.set(
                    message.getChatId().toString(),
                    new UserState(UUID.fromString(userId), null, null))))
                .then(singleUseLinkService.deleteByKey(parts[1]))
                    .then(botSender.sendText(
                        message.getChatId(),
                        "Телеграм успешно привязан!",
                        KeyboardFactory.getMainKeyboard()))
                    .then(dislikeCommand.execute(message))
                    .onErrorResume(err -> {
                        log.warn(err.getMessage());
                        return Mono.empty();
                    });
    }

    @Override
    public Mono<Void> execute(CallbackQuery callbackQuery) {
        return Mono.empty();
    }

    @Override
    public Boolean canExecute(Update update) {
        return update.hasMessage() && update.getMessage().getText().startsWith("/start");
    }

}
