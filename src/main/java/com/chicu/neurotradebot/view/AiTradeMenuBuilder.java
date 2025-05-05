// src/main/java/com/chicu/neurotradebot/view/AiTradeMenuBuilder.java
package com.chicu.neurotradebot.view;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

@Component
public class AiTradeMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_control");  // чтобы GenericMenuCallbackHandler его поймал, если вы его используете
    }

    public String title() {
        return "🤖 Настройки AI-режима";
    }

    public InlineKeyboardMarkup markup(Long chatId) {
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(InlineKeyboardButton.builder()
                    .text("📈 Вкл/выкл AI")
                    .callbackData("toggle_ai")
                    .build()),
                List.of(InlineKeyboardButton.builder()
                    .text("⬅️ Назад")
                    .callbackData("start_menu")
                    .build())
            ))
            .build();
    }
}
