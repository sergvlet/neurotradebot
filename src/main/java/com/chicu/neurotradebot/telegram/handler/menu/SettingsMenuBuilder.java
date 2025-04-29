package com.chicu.neurotradebot.telegram.handler.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SettingsMenuBuilder {

    public InlineKeyboardMarkup buildSettingsMenu() {
        InlineKeyboardButton switchModeButton = InlineKeyboardButton.builder()
                .text("🔄 Переключить режим торговли")
                .callbackData("switch_mode")
                .build();

        InlineKeyboardButton selectExchangeButton = InlineKeyboardButton.builder()
                .text("🌐 Выбрать биржу")
                .callbackData("select_exchange")
                .build();

        InlineKeyboardButton apiKeySetupButton = InlineKeyboardButton.builder()
                .text("🔑 Настроить API-ключи")
                .callbackData("api_setup_start")
                .build();

        InlineKeyboardButton manualModeButton = InlineKeyboardButton.builder()
                .text("🧑‍💻 Ручная торговля")
                .callbackData("select_manual_mode")
                .build();

        InlineKeyboardButton aiModeButton = InlineKeyboardButton.builder()
                .text("🤖 AI-торговля")
                .callbackData("select_ai_mode")
                .build();

        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("🔙 Назад в главное меню")
                .callbackData("back_to_main")
                .build();

        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(switchModeButton),
                List.of(selectExchangeButton),
                List.of(apiKeySetupButton),
                List.of(manualModeButton, aiModeButton),
                List.of(backButton)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}