// src/main/java/com/chicu/neurotradebot/view/ManualTradeMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.manualtredemenu;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

@Component
public class ManualTradeMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("manual_trade_menu");
    }

    public String title() {
        return "💹 Меню ручной торговли";
    }

    public InlineKeyboardMarkup markup(Long chatId) {
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(InlineKeyboardButton.builder()
                    .text("➕ Открыть позицию")
                    .callbackData("open_manual")
                    .build()),
                List.of(InlineKeyboardButton.builder()
                    .text("⬅️ Назад")
                    .callbackData("start_menu")
                    .build())
            ))
            .build();
    }
}
