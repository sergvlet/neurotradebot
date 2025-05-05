// src/main/java/com/chicu/neurotradebot/view/StartMenuBuilder.java
package com.chicu.neurotradebot.telegram.view;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

@Component
public class StartMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of(); // необязательно
    }

    public String title() {
        return "🚀 Главное меню";
    }

    public InlineKeyboardMarkup markup(Long chatId) {
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(InlineKeyboardButton.builder()
                    .text("🤖 AI-торговля")
                    .callbackData("ai_control")
                    .build()),
                List.of(InlineKeyboardButton.builder()
                    .text("💹 Ручная торговля")
                    .callbackData("manual_trade_menu")
                    .build())
            ))
            .build();
    }
}
