package com.chicu.neurotradebot.telegram.view.aimenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AiTradeMenuBuilder implements MenuDefinition {

    private final AiTradeSettingsService settingsService;

    @Override
    public Set<String> keys() {
        return Set.of("ai_control");
    }

    @Override
    public String title() {
        return "🤖 Настройки AI-режима";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // Получаем или создаём настройки для этого чата
        AiTradeSettings settings = settingsService.getForCurrentUser();
        boolean enabled = settings.isEnabled();

        // Кнопки меню
        var toggleAi   = InlineKeyboardButton.builder().text("🤖 Вкл/Выкл AI-режим").callbackData("toggle_ai").build();
        var tradeMode  = InlineKeyboardButton.builder().text("🔀 Режим торговли").callbackData("ai_trade_mode").build();
        var pairs      = InlineKeyboardButton.builder().text("💱 Валютные пары").callbackData("ai_pairs").build();
        var strategy   = InlineKeyboardButton.builder().text("🧠 Стратегии AI").callbackData("ai_strategy").build();
        var risk       = InlineKeyboardButton.builder().text("⚠️ Управление рисками").callbackData("ai_risk").build();
        var scan       = InlineKeyboardButton.builder().text("⏱ Интервал сканирования").callbackData("ai_scan_interval").build();
        var notify     = InlineKeyboardButton.builder().text("🔔 Уведомления").callbackData("ai_notify").build();
        var profiles   = InlineKeyboardButton.builder().text("💾 Профили").callbackData("ai_profiles").build();
        var autoConfig = InlineKeyboardButton.builder().text("✨ Автонастройка AI").callbackData("ai_autoconfig").build();
        var analytics  = InlineKeyboardButton.builder().text("📊 Аналитика").callbackData("ai_analytics").build();
        var backtest   = InlineKeyboardButton.builder().text("🧪 Backtest").callbackData("ai_backtest").build();
        var positions  = InlineKeyboardButton.builder().text("🗂 Позиции").callbackData("ai_positions").build();
        var history    = InlineKeyboardButton.builder().text("📜 История").callbackData("ai_history").build();
        var help       = InlineKeyboardButton.builder().text("❓ Помощь").callbackData("ai_help").build();
        var back       = InlineKeyboardButton.builder().text("⬅️ Назад").callbackData("start_menu").build();

        // Динамическая кнопка запуска/остановки торговли
        String btnText = enabled ? "⏹️ Остановить торговлю" : "🚀 Запустить торговлю";
        var startStop  = InlineKeyboardButton.builder()
                .text(btnText)
                .callbackData("ai_trade_toggle")
                .build();

        // Собираем ряды
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(toggleAi));
        rows.add(List.of(tradeMode, pairs));
        rows.add(List.of(strategy, risk));
        rows.add(List.of(scan, notify));
        rows.add(List.of(profiles, autoConfig));
        rows.add(List.of(analytics, backtest));
        rows.add(List.of(positions, history));
        rows.add(List.of(startStop)); // вставили наш ряд
        rows.add(List.of(help));
        rows.add(List.of(back));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
