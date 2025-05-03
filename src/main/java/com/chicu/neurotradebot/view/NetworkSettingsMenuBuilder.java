// src/main/java/com/chicu/neurotradebot/view/NetworkSettingsMenuBuilder.java
package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class NetworkSettingsMenuBuilder {

    /**
     * @param testMode true — выбран тестнет, false — выбран реал
     */
    public InlineKeyboardMarkup buildNetworkSettingsMenu(boolean testMode) {
        InlineKeyboardButton toggle = InlineKeyboardButton.builder()
                .text(testMode ? "🔵 Тестнет (✓)" : "🟢 Реал (✓)")
                .callbackData("toggle_mode")
                .build();


        InlineKeyboardButton selectExchange = InlineKeyboardButton.builder()
                .text("🌐 Выбрать биржу")
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
                        List.of(selectExchange),
                        List.of(apiSetup),
                        List.of(back)
                ))
                .build();
    }
}
