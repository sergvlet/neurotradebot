// src/main/java/com/chicu/neurotradebot/telegram/view/PairsAddMethodMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

/**
 * Подменю: способ добавления новых торговых пар.
 * 1) Импорт с биржи
 * 2) Ввод вручную (одной или через запятую список)
 * 3) Автоматический подбор ботом
 */
@Component
public class PairsAddMethodMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("pairs_add_method");
    }

    @Override
    public String title() {
        return "Как добавить пары?";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("🔗 Импортировать с биржи")
                                        .callbackData("pairs_import")
                                        .build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("✍️ Ввести вручную")
                                        .callbackData("pairs_manual_entry")
                                        .build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("🤖 Подобрать автоматически")
                                        .callbackData("pairs_autoconfig")
                                        .build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("⬅️ Отмена")
                                        .callbackData("apply_network_settings_ai")
                                        .build()
                        )
                ))
                .build();
    }
}

