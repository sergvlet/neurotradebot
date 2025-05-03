// src/main/java/com/chicu/neurotradebot/view/ModeMenuBuilder.java
package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class ModeMenuBuilder {

    /**
     * @param testMode true — тестовый режим выбран, false — реальный режим
     */
    public InlineKeyboardMarkup buildModeMenu(boolean testMode) {
        InlineKeyboardButton realButton = InlineKeyboardButton.builder()
            .text((!testMode ? "✅ " : "") + "🟢 Реальный режим")
            .callbackData("mode_real")
            .build();

        InlineKeyboardButton testButton = InlineKeyboardButton.builder()
            .text(( testMode ? "✅ " : "") + "🔵 Тестовый режим")
            .callbackData("mode_test")
            .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("🔙 Назад")
            .callbackData("back_to_settings")
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(realButton),
                List.of(testButton),
                List.of(back)
            ))
            .build();
    }
}
