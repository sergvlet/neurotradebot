package com.chicu.neurotradebot.telegram.view;// src/main/java/com/chicu/neurotradebot/view/AiTradeMenuBuilder.java

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

@Component
public class AiTradeMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_control");
    }

    public String title() {
        return "🤖 Настройки AI-режима";
    }

    public InlineKeyboardMarkup markup(Long chatId) {
        // Основные настройки
        var toggleAi = InlineKeyboardButton.builder()
                .text("🤖 Включить или отключить AI-режим")
                .callbackData("toggle_ai")
                .build();

        var tradeMode = InlineKeyboardButton.builder()
                .text("🔀 Режим торговли (Spot/Futures/Margin)")
                .callbackData("ai_trade_mode")
                .build();

        var pairs = InlineKeyboardButton.builder()
                .text("💱 Валютные пары")
                .callbackData("ai_pairs")
                .build();

        var strategy = InlineKeyboardButton.builder()
                .text("🧠 Стратегии AI")
                .callbackData("ai_strategy")
                .build();
        var risk = InlineKeyboardButton.builder()
                .text("⚠️ Управление рисками")
                .callbackData("ai_risk")
                .build();

        var scan = InlineKeyboardButton.builder()
                .text("⏱ Интервал сканирования")
                .callbackData("ai_scan_interval")
                .build();
        var notify = InlineKeyboardButton.builder()
                .text("🔔 Настройка уведомлений")
                .callbackData("ai_notify")
                .build();

        // Профили и автонастройка
        var profiles = InlineKeyboardButton.builder()
                .text("💾 Профили настроек")
                .callbackData("ai_profiles")
                .build();
        var autoConfig = InlineKeyboardButton.builder()
                .text("✨ Автонастройка AI")
                .callbackData("ai_autoconfig")
                .build();

        // Мониторинг и анализ
        var analytics = InlineKeyboardButton.builder()
                .text("📊 Графический анализ")
                .callbackData("ai_analytics")
                .build();
        var backtest = InlineKeyboardButton.builder()
                .text("🧪 Запустить Backtest")
                .callbackData("ai_backtest")
                .build();

        var positions = InlineKeyboardButton.builder()
                .text("🗂 Текущие позиции")
                .callbackData("ai_positions")
                .build();
        var history = InlineKeyboardButton.builder()
                .text("📜 История сделок")
                .callbackData("ai_history")
                .build();

        // Справка и возврат
        var help = InlineKeyboardButton.builder()
                .text("❓ Помощь / FAQ")
                .callbackData("ai_help")
                .build();
        var back = InlineKeyboardButton.builder()
                .text("⬅️ Назад")
                .callbackData("start_menu")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        // 1. Включение AI
                        List.of(toggleAi),

                        // 2. Выбор режима и пар
                        List.of(tradeMode, pairs),

                        // 3. Стратегия и риски
                        List.of(strategy, risk),

                        // 4. Интервал и уведомления
                        List.of(scan, notify),

                        // 5. Профили и автонастройка
                        List.of(profiles, autoConfig),

                        // 6. Графический анализ и backtest
                        List.of(analytics, backtest),

                        // 7. Мониторинг позиций и истории
                        List.of(positions, history),

                        // 8. Помощь
                        List.of(help),

                        // 9. Возврат в главное меню
                        List.of(back)
                ))
                .build();
    }
}
