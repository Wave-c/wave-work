package com.wave.notification_service.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class BotService extends TelegramWebhookBot {
    private final String username;

    public BotService(String username, String token, String webhookPath,
        DefaultBotOptions options, String webhookSecret) {
        super(options, token);
        this.username = username;

        try {
            SetWebhook setWebhook = SetWebhook.builder()
                .url(webhookPath)
                .secretToken(webhookSecret)
                .build();
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error("Failed to set webhook", e);
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/message";
    }

    @Override
    public String getBotUsername() {
        return username;
    }

}
