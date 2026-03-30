package com.wave.notification_service.components.web;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.wave.notification_service.dtos.PatchProfileDto;

import reactor.core.publisher.Mono;

@Component
public class ProfileWebClient {
    private final WebClient webClient;

    public ProfileWebClient(
        @Qualifier("profileServiceWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }

    public Mono<Void> setTelegramUsername(UUID userId, String telegramUsername) {
        return webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/profiles/me")
                .build()
            )
            .header("X-User-Id", userId.toString())
            .body(Mono.just(new PatchProfileDto()
                .setTelegramUsername(telegramUsername)),
                PatchProfileDto.class)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> setEmail(UUID userId, String email) {
        return webClient.patch()
            .uri(uriBuilder -> uriBuilder
                .path("/profiles/me")
                .build()
            )
            .header("X-User-Id", userId.toString())
            .body(Mono.just(new PatchProfileDto()
                .setEmail(email)),
                PatchProfileDto.class)
            .retrieve()
            .bodyToMono(Void.class);
    }
}
