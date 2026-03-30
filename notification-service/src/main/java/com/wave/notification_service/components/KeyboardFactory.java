package com.wave.notification_service.components;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class KeyboardFactory {
    public static ReplyKeyboardMarkup getMainKeyboard() {
        return ReplyKeyboardMarkup.builder()
            .keyboardRow(new KeyboardRow(List.of(
                new KeyboardButton("👍"),
                new KeyboardButton("👎")
            )))
            .resizeKeyboard(true)
            .selective(true)
            .build();
    }

    public static InlineKeyboardMarkup getTaskKeyboard(String url) {
        return InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(
                InlineKeyboardButton.builder()
                    .text("go")
                    .url(url)
                    .build()
            ))
            .build();
    }
}
