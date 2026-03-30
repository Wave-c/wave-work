package com.wave.notification_service.components.web;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.wave.notification_service.dtos.NextRecommendedWorkResponse;

import reactor.core.publisher.Mono;

@Component
public class RecomendationWebClient {
    private final WebClient webClient;

    public RecomendationWebClient(
        @Qualifier("recomendationServiceWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }

    public Mono<NextRecommendedWorkResponse> getNext(UUID userId, String currentCursor) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/recommendations/me/cursor")
                .queryParam("subjectType", "JOB")
                .build()
            )
            .header("X-User-Id", userId.toString())
            .header("X-Cursor", currentCursor)
            .retrieve()
            .bodyToMono(NextRecommendedWorkResponse.class);
    }
}
