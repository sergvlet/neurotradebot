// src/main/java/com/chicu/neurotradebot/telegram/view/manualtredemenu/ManualStrategiesMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.manualtredemenu;

import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ManualStrategiesMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("manual_strategies");
    }

    @Override
    public String title() {
        return "🧠 Выберите стратегию (Manual)";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        List<List<InlineKeyboardButton>> rows = Arrays.stream(StrategyType.values())
            .map(strategy ->
                List.of(InlineKeyboardButton.builder()
                    .text(strategy.getDisplayName())
                    .callbackData("manual_strat_" + strategy.name())
                    .build()
                )
            )
            .collect(Collectors.toList());

        // кнопка «Назад» к меню ручной торговли
        rows.add(List.of(InlineKeyboardButton.builder()
            .text("⬅️ Назад")
            .callbackData("manual_trade_menu")
            .build()
        ));

        return InlineKeyboardMarkup.builder()
            .keyboard(rows)
            .build();
    }
}
