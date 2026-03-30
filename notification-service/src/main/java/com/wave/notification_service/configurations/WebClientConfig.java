package com.wave.notification_service.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.recomendation-service.url}")
    private String recomendationServiceUrl;
    @Value("${services.profile-service.url}")
    private String profileServiceUrl;

    @Bean("recomendationServiceWebClient")
    public WebClient recomendationServiceWebClient() {
        return WebClient.builder()
            .baseUrl(recomendationServiceUrl)
            .build();
    }

    @Bean("profileServiceWebClient")
    public WebClient profileServiceWebClient() {
        return WebClient.builder()
            .baseUrl(profileServiceUrl)
            .build();
    }
}
