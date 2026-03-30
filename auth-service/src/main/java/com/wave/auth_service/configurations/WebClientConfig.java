package com.wave.auth_service.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.profile-service.url}")
    private String profileServiceUrl;

    @Bean(name = "profile-service-webclient")
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(profileServiceUrl)
            .build();
    }
}
