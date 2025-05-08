// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strategyMenu/StrategyMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu;

import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StrategyMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_strategy");
    }

    @Override
    public String title() {
        return "🔧 Выберите стратегии (несколько):";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        // по 2 кнопки на строку
        List<StrategyType> types = List.of(StrategyType.values());
        for (int i = 0; i < types.size(); i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 2 && i + j < types.size(); j++) {
                StrategyType t = types.get(i + j);
                row.add(InlineKeyboardButton.builder()
                    .text(t.name().replace('_', ' '))
                    .callbackData("strat_toggle_" + t.name())
                    .build());
            }
            rows.add(row);
        }
        // кнопка готово
        rows.add(List.of(
            InlineKeyboardButton.builder()
                .text("✅ Готово")
                .callbackData("strat_done")
                .build()
        ));
        return InlineKeyboardMarkup.builder()
            .keyboard(rows)
            .build();
    }
}
