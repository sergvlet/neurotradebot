package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * Строит меню выбора языка.
 */
@Component
public class LanguageMenuBuilder {

    public InlineKeyboardMarkup buildLanguageMenu() {
        InlineKeyboardButton ruButton = InlineKeyboardButton.builder()
                .text("🇷🇺 Русский")
                .callbackData("language_ru")
                .build();

        InlineKeyboardButton enButton = InlineKeyboardButton.builder()
                .text("🇬🇧 English")
                .callbackData("language_en")
                .build();

        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("back_to_main")
                .build();

        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(ruButton),
                List.of(enButton),
                List.of(backButton)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
