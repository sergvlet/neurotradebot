// src/main/java/com/chicu/neurotradebot/telegram/view/StrategyMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu;

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
public class StrategyMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_strategy");
    }

    @Override
    public String title() {
        return "🧠 Выберите стратегию";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // Для каждого значения StrategyType создаём кнопку mode_<имя>
        List<List<InlineKeyboardButton>> rows = Arrays.stream(StrategyType.values())
            .map(strategy ->
                List.of(InlineKeyboardButton.builder()
                    .text(strategy.getDisplayName())       // Выводимое название в enum
                    .callbackData("strat_" + strategy.name())
                    .build()
                )
            )
            .collect(Collectors.toList());

        // Добавляем кнопку «Назад» внизу
        rows.add(List.of(InlineKeyboardButton.builder()
            .text("⬅️ Назад")
            .callbackData("ai_control")
            .build()
        ));

        return InlineKeyboardMarkup.builder()
            .keyboard(rows)
            .build();
    }
}
