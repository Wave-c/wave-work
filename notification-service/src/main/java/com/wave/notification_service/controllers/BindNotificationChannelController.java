package com.wave.notification_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.notification_service.services.EmailCodesService;
import com.wave.notification_service.services.SingleUseLinkService;
import com.wave.notification_service.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;



@Log4j2
@RestController
@RequestMapping("/bind")
@RequiredArgsConstructor
public class BindNotificationChannelController {
    private final SingleUseLinkService singleUseLinkService;
    private final EmailCodesService emailCodesService;
    private final UserService userService;
    @Value("${telegram.username}")
    private String botUsername;

    @GetMapping("/get-tg-link")
    public Mono<ResponseEntity<String>> getTgLink(@RequestHeader("X-User-Id") UUID userId) {
        log.info("UserId: {}", userId);
        return singleUseLinkService.getNew(userId)
            .map(link -> ResponseEntity.ok("https://t.me/" + botUsername +
                "?start=" + link));
    }

    @PostMapping("/unbind-telegram")
    public Mono<ResponseEntity<String>> unbindTg(@RequestHeader("X-User-Id") UUID userId) {
        //TODO: process POST request

        return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(500))
            .body("Endpoint unimplemented!"));
    }

    @GetMapping("/send-email-confirm")
    public Mono<ResponseEntity<Void>> sendEmailConfirm(
        @RequestHeader("X-User-Id") UUID userId,
        @RequestParam String email
    ) {
        return emailCodesService.getNew(userId, email)
            .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/confirm-email-code")
    public Mono<ResponseEntity<Boolean>> confirmEmailCode(
        @RequestHeader("X-User-Id") UUID userId,
        @RequestBody String code) {
        log.info("verify code {} for user {}", code, userId);
        return emailCodesService.getByKey(userId.toString())
            .flatMap(correctCode -> {
                if (correctCode.getCode().contentEquals(code)) {
                    log.info(true);
                    return userService.setEmail(userId, correctCode.getEmail())
                        .thenReturn(ResponseEntity.ok(true));
                } else {
                    return Mono.error(new RuntimeException("Code is incorrect"));
                }
            })
            .onErrorResume(RuntimeException.class, e ->
                Mono.just(ResponseEntity.badRequest().body(false)))
            .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body(false)));
    }

}
