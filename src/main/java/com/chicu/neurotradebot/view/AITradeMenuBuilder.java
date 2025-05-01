package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class AITradeMenuBuilder {

    public InlineKeyboardMarkup buildMainMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("🤖 Управление AI", "ai_control")),

                        List.of(button("📈 Изменить тип торговли", "ai_trading_type")),
                        List.of(button("🎯 Изменить стратегию", "ai_strategy")),
                        List.of(button("⚖️ Изменить риск", "ai_risk")),
                        List.of(button("💱 Выбрать валюту", "ai_pair")),
                        List.of(button("🔔 Уведомления", "ai_notifications")),
                        List.of(button("📊 Визуализация AI", "ai_visual")),

                        List.of(button("🔙 Назад", "back_to_settings"))


                        ))
                .build();
    }

    public InlineKeyboardMarkup buildStrategySelectionMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("🎯 RSI + EMA", "ai_strategy_rsi_ema")),
                        List.of(button("🧊 Grid стратегия", "ai_strategy_grid")), // позже
                        List.of(button("📉 DCA стратегия", "ai_strategy_dca")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }


    public InlineKeyboardMarkup buildTradingTypeMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("📈 Спотовая торговля", "ai_trading_type_spot")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }

    public InlineKeyboardMarkup buildStartStopMenu(boolean running) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button(running ? "🔴 Остановить AI" : "🟢 Запустить AI", running ? "ai_stop" : "ai_start")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }



    public InlineKeyboardMarkup buildRiskSelectionMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("🟢 Низкий риск", "ai_risk_low")),
                        List.of(button("🟡 Средний риск", "ai_risk_medium")),
                        List.of(button("🔴 Высокий риск", "ai_risk_high")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }



    public InlineKeyboardMarkup buildNotificationsMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("🔔 Включить уведомления", "ai_notifications_on")),
                        List.of(button("🔕 Отключить уведомления", "ai_notifications_off")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }

    public InlineKeyboardMarkup buildPairSelectionMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("🔹 Выбрать вручную", "ai_pair_mode_manual")),
                        List.of(button("📃 Выбрать из списка", "ai_pair_mode_list")),
                        List.of(button("🤖 Автоматический выбор", "ai_pair_mode_auto")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }

    public InlineKeyboardMarkup buildManualPairSelectionMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("BTC/USDT", "ai_manual_pair_BTCUSDT"), button("ETH/USDT", "ai_manual_pair_ETHUSDT")),
                        List.of(button("BNB/USDT", "ai_manual_pair_BNBUSDT"), button("SOL/USDT", "ai_manual_pair_SOLUSDT")),
                        List.of(button("XRP/USDT", "ai_manual_pair_XRPUSDT"), button("ADA/USDT", "ai_manual_pair_ADAUSDT")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }

    public InlineKeyboardMarkup buildListPairMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("➕ Добавить пары", "ai_list_add")),
                        List.of(button("📜 Выбрать из списка", "ai_list_pick")),
                        List.of(button("➖ Удалить пары", "ai_list_remove")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }



    public InlineKeyboardMarkup buildListSelectMenu(List<String> lists) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            String text = lists.get(i);
            rows.add(List.of(button(text, "ai_list_select_" + i)));
        }
        rows.add(List.of(button("🔙 Назад", "ai_back_list_menu")));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public InlineKeyboardMarkup buildListRemoveMenu(List<String> lists) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            rows.add(List.of(button("❌ " + lists.get(i), "ai_list_del_item_" + i)));
        }
        rows.add(List.of(button("🔙 Назад", "ai_back_list_menu")));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
    public InlineKeyboardMarkup buildBackToMainMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }

}
