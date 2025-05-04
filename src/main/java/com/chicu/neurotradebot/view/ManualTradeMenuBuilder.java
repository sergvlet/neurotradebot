// src/main/java/com/chicu/neurotradebot/view/ManualTradeMenuBuilder.java
package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class ManualTradeMenuBuilder {

    public String title() {
        return "⚙️ Ручной режим торговли";
    }

    public InlineKeyboardMarkup build(Long chatId) {
        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(InlineKeyboardButton.builder().text("📥 Открыть сделку").callbackData("manual_open").build()),
                List.of(InlineKeyboardButton.builder().text("📤 Закрыть сделку").callbackData("manual_close").build()),
                List.of(InlineKeyboardButton.builder().text("📊 Статистика").callbackData("manual_stats").build()),
                List.of(InlineKeyboardButton.builder().text("⬅️ Назад").callbackData("back_to_settings").build())
        );
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
}
