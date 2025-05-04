package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class NetworkSettingsMenuBuilder {

    /**
     * @param testMode true — выбран тестнет, false — реал
     * @param exchange текущая выбранная биржа (nullable)
     */
    public InlineKeyboardMarkup buildNetworkSettingsMenu(boolean testMode, String exchange) {
        InlineKeyboardButton toggle = InlineKeyboardButton.builder()
                .text(testMode ? "🔵 Тестнет (✓)" : "🟢 Реал (✓)")
                .callbackData("toggle_mode")
                .build();

        InlineKeyboardButton exchangeBtn = InlineKeyboardButton.builder()
                .text("🌐 Биржа: " + (exchange != null ? exchange : "не выбрана"))
                .callbackData("select_exchange")
                .build();

        InlineKeyboardButton apiSetup = InlineKeyboardButton.builder()
                .text("🔑 Настроить API-ключи")
                .callbackData("api_setup_start")
                .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("back_to_main")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(toggle),
                        List.of(exchangeBtn),
                        List.of(apiSetup),
                        List.of(back)
                ))
                .build();
    }
}
