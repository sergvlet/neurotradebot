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

    @Override
    public String title() {
        return "💹 Меню ручной торговли";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                // Новая строка с кнопкой «🧠 Стратегии»
                List.of(
                    InlineKeyboardButton.builder()
                        .text("🧠 Стратегии")
                        .callbackData("manual_strategies")
                        .build()
                ),
                // Существующая кнопка «Назад»
                List.of(
                    InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("start_menu")
                        .build()
                )
            ))
            .build();
    }
}
