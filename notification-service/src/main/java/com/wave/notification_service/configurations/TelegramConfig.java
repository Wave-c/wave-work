package com.wave.notification_service.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import com.wave.notification_service.services.BotService;

@Configuration
public class TelegramConfig {
    @Value("${telegram.username}")
    private String username;
    @Value("${telegram.token}")
    private String token;
    @Value("${telegram.webhook-path}")
    private String webhookPath;
    @Value("${telegram.webhook-secret-token}")
    private String webhookSecret;

    @Bean
    public BotService botService() {
        return new BotService(username, token,
            webhookPath, defaultBotOptions(),
            webhookSecret);
    }

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setBaseUrl("http://192.168.1.10:8080/telegram/bot");
        return options;
    }
}
