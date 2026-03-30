package com.wave.notification_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.wave.notification_service.components.UpdateProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Log4j2
@RestController
@RequestMapping("/callback")
@RequiredArgsConstructor
public class TelegramController {
    private final UpdateProcessor updateProcessor;

    @PostMapping("/message")
    public Mono<ResponseEntity<Void>> updateReceived(@RequestBody Update update) {
        log.info("Received update");
        return updateProcessor.processUpdate(update)
            .thenReturn(ResponseEntity.ok().build());
    }

}
