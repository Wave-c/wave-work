package com.wave.auth_service.helpers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.wave.auth_service.dtos.CreateProfileRequest;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class ProfileWebClient {
    private final WebClient webClient;

    public ProfileWebClient(
        @Qualifier("profile-service-webclient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> createProfile(UUID userId, String username) {
        log.info("Create profile for {} {}", userId, username);
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/profiles/create_profile")
                .build()
            )
            .body(Mono.just(new CreateProfileRequest(username)), CreateProfileRequest.class)
            .header("x-user-id", userId.toString())
            .retrieve()
            .bodyToMono(Void.class)
            .onErrorResume(err -> {
                log.error(err);
                return Mono.empty();
            });
    }
}
