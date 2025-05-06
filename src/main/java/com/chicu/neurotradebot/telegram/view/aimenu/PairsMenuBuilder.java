// src/main/java/com/chicu/neurotradebot/telegram/view/PairsMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

/**
 * Меню управления списком торговых пар:
 * – добавить новую пару
 * – удалить существующую
 * – вернуться назад
 */
@Component
public class PairsMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_pairs");
    }

    @Override
    public String title() {
        return "💱 Валютные пары";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // Добавляем 2 кнопки: одна для добавления, другая — для удаления,
        // и одну «Назад» внизу.
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder()
                        .text("➕ Добавить пару")
                        .callbackData("pairs_add")
                        .build(),
                    InlineKeyboardButton.builder()
                        .text("➖ Удалить пару")
                        .callbackData("pairs_remove")
                        .build()
                ),
                List.of(
                    InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("ai_control")
                        .build()
                )
            ))
            .build();
    }
}
