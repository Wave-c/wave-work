package com.wave.notification_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.wave.notification_service.services.SingleUseLinkService;
import com.wave.notification_service.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Log4j2
@RestController
@RequestMapping("/callback")
@RequiredArgsConstructor
public class TelegramController {
    private final UserService userService;
    private final SingleUseLinkService singleUseLinkService;

    @PostMapping("/message")
    public Mono<ResponseEntity<Void>> updateReceived(@RequestBody Update update) {
        log.info("Received update");
        if (update.getMessage().getText().startsWith("/start")) {
            log.info(update.getMessage().getText());
            return singleUseLinkService.getByKey(
                update.getMessage().getText().split(" ")[1])
                .flatMap(userId -> userService.setTelegramChatId(
                    UUID.fromString(userId),
                    update.getMessage().getChatId()))
                    .then(singleUseLinkService.deleteByKey(
                        update.getMessage().getText().split(" ")[1]))
                        .thenReturn(ResponseEntity.ok().<Void>build())
                        .onErrorResume(err -> {
                            log.warn(err.getMessage());
                            return Mono.just(ResponseEntity.ok().<Void>build());
                        });
        }
        return Mono.just(ResponseEntity.ok().build());
    }

}
