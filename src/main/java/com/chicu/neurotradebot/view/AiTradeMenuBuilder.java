// src/main/java/com/chicu/neurotradebot/view/AiTradeMenuBuilder.java
package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class AiTradeMenuBuilder {

    public String title() {
        return "🤖 Настройки AI-режима";
    }

    public InlineKeyboardMarkup build(Long chatId) {
        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(InlineKeyboardButton.builder().text("📈 Включить/выключить AI").callbackData("ai_control").build()),
                List.of(InlineKeyboardButton.builder().text("📊 Визуализация графика").callbackData("ai_visual").build()),
                List.of(InlineKeyboardButton.builder().text("💱 Валютные пары").callbackData("ai_pairs").build()),
                List.of(InlineKeyboardButton.builder().text("🧠 Стратегия").callbackData("ai_strategy").build()),
                List.of(InlineKeyboardButton.builder().text("⚠️ Риски").callbackData("ai_risk").build()),
                List.of(InlineKeyboardButton.builder().text("🔔 Уведомления").callbackData("ai_notify").build()),
                List.of(InlineKeyboardButton.builder().text("⏱ Частота сканирования").callbackData("ai_scan_interval").build()),
                List.of(InlineKeyboardButton.builder().text("📉 Статистика").callbackData("ai_stats").build()),
                List.of(InlineKeyboardButton.builder().text("⬅️ Назад").callbackData("back_to_settings").build())
        );
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
}
