// src/main/java/com/chicu/neurotradebot/view/AITradeMenuBuilder.java
package com.chicu.neurotradebot.view;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class AITradeMenuBuilder {

    /** 
     * @param settings — содержит флаг testMode 
     */
    public InlineKeyboardMarkup buildAiSettingsMenu(AiTradeSettings settings) {
        InlineKeyboardButton toggle = InlineKeyboardButton.builder()
                .text(settings.isTestMode()
                        ? "🔵 Тестнет (✓)"
                        : "🟢 Реал (✓)")
                .callbackData("ai_toggle_mode")
                .build();

        InlineKeyboardButton pair = InlineKeyboardButton.builder()
                .text("💱 Выбрать валюту")
                .callbackData("ai_pair")
                .build();

        // ... остальные ваши кнопки AI-меню
        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("ai_back_main")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(toggle),
                        List.of(pair),
                        // … другие кнопки …
                        List.of(back)
                ))
                .build();
    }

    // существующие методы buildMainMenu(), buildTradingTypeMenu() и т.д.
}
