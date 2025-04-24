package com.chicu.neurotradebot.telegram.util;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.ai.strategy.config.AdxConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.telegram.callback.BotCallback;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.enums.TradeType;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import com.chicu.neurotradebot.trade.service.UserStrategyConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeyboardService {

    private final UserSettingsService userSettingsService;
    private final UserStrategyConfigService userStrategyConfigService; // Подключаем новый сервис

    public InlineKeyboardMarkup getMainMenu(Long chatId) {
        return buildKeyboard(List.of(
                List.of(createButton("🚀 Начать", BotCallback.TRADING_MENU.getValue())),  // Кнопка теперь вызывает торговое меню
                List.of(createButton("💳 Подписка", BotCallback.SUBSCRIBE.getValue())),
                List.of(createButton("🤖 О боте", BotCallback.BOT_INFO.getValue()))
        ));
    }

    public InlineKeyboardMarkup getTradingMenuByMode(Long chatId) {
        TradeType type = userSettingsService.getTradeType(chatId);
        return type == TradeType.MANUAL ? getManualTradingMenu(chatId) : getTradingMenu(chatId);
    }

    // Обновленный метод getTradingMenu
    public InlineKeyboardMarkup getTradingMenu(Long chatId) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // Выбираем режим торговли
        buttons.add(List.of(createButton("🔄 Выбрать режим торговли", BotCallback.SET_TRADE_TYPE.getValue())));

        // Статистика и история
        buttons.add(List.of(
                createButton("📈 Статистика", BotCallback.STATISTICS.getValue()),
                createButton("📉 История", BotCallback.HISTORY.getValue())
        ));

        // Переключение между AI и ручным режимами
        TradeType current = userSettingsService.getTradeType(chatId);
        buttons.add(List.of(
                createButton((current == TradeType.AI ? "✅ " : "☑️ ") + "🤖 AI-режим", BotCallback.SET_TRADE_TYPE.getValue() + ":AI"),
                createButton((current == TradeType.MANUAL ? "✅ " : "☑️ ") + "🧑‍💼 Ручной режим", BotCallback.SET_TRADE_TYPE.getValue() + ":MANUAL")
        ));

        // Кнопка назад
        buttons.add(List.of(createButton("🔙 Назад", BotCallback.MAIN_MENU.getValue())));

        return buildKeyboard(buttons);
    }

    public InlineKeyboardMarkup getManualTradingMenu(Long chatId) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(createButton("🔄 Выполнить сделку", BotCallback.MANUAL_TRADE_EXECUTE.getValue())));
        buttons.add(List.of(
                createButton("📈 Статистика", BotCallback.MANUAL_TRADE_STATS.getValue()),
                createButton("📉 История", BotCallback.MANUAL_TRADE_HISTORY.getValue())
        ));
        buttons.add(List.of(createButton("⚙️ Настройки", BotCallback.MANUAL_TRADE_SETTINGS.getValue())));
        buttons.add(List.of(createButton("🔙 Назад", BotCallback.START_TRADE.getValue())));
        return buildKeyboard(buttons);
    }

    public InlineKeyboardMarkup getManualTradeSettingsMenu(Long chatId) {
        var settings = userSettingsService.getOrCreate(chatId);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(createButton("🧠 Стратегии", BotCallback.STRATEGY_MENU.getValue())));
        buttons.add(List.of(createButton("🧪 Режим торговли", BotCallback.SELECT_MODE.getValue())));
        buttons.add(List.of(createButton("💵 Лимит сделки", BotCallback.TRADE_LIMIT.getValue())));
        buttons.add(List.of(createButton("📊 Пара: " + settings.getExchangeSymbol(), BotCallback.SYMBOL_MENU.getValue())));
        buttons.add(List.of(createButton("⏱ Таймфрейм: " + settings.getTimeframe(), BotCallback.TIMEFRAME_MENU.getValue())));
        buttons.add(List.of(createButton("📈 Биржа", BotCallback.EXCHANGE_MENU.getValue())));
        buttons.add(List.of(createButton("💰 Баланс", BotCallback.SHOW_BALANCE.getValue())));
        buttons.add(List.of(createButton("🔙 Назад", BotCallback.START_TRADE.getValue())));
        return buildKeyboard(buttons);
    }

    public InlineKeyboardMarkup getStrategySelectionMenu(Set<AvailableStrategy> selected) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (AvailableStrategy strategy : AvailableStrategy.values()) {
            boolean isSelected = selected.contains(strategy);
            String prefix = isSelected ? "✅ " : "☑️ ";
            String text = prefix + strategy.getTitle();
            String callback = BotCallback.TOGGLE_STRATEGY.getValue() + ":" + strategy.name();
            rows.add(List.of(createButton(text, callback)));
        }
        return appendBackButton(rows);
    }

    // Метод для получения клавиатуры с выбором торгового режима
    public InlineKeyboardMarkup getModeSelectionMenu(Long chatId, TradeMode selected) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // Переключение между режимами
        for (TradeMode mode : TradeMode.values()) {
            String prefix = mode == selected ? "✅ " : "☑️ ";
            String text = prefix + mode.getTitle();
            String callback = BotCallback.TOGGLE_MODE.getValue() + ":" + mode.name();
            buttons.add(List.of(createButton(text, callback)));
        }

        // Кнопка для отображения графиков или информации о стратегии
        buttons.add(List.of(createButton("📊 Показать график", BotCallback.SHOW_STRATEGY_GRAPH.getValue())));

        return appendBackButton(buttons);  // Добавляем кнопку "Назад"
    }

    public InlineKeyboardMarkup getStrategyConfigMenu(Long chatId, String strategyName, StrategyConfig config) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // Пример для стратегии Adx
        if ("Adx".equals(strategyName)) {
            AdxConfig adxConfig = (AdxConfig) config;
            buttons.add(List.of(createButton("Период: " + adxConfig.getPeriod(), BotCallback.CONFIG_UPDATE.getValue() + ":Adx:period")));
            buttons.add(List.of(createButton("Порог силы тренда: " + adxConfig.getTrendStrengthThreshold(), BotCallback.CONFIG_UPDATE.getValue() + ":Adx:trendStrengthThreshold")));
        }

        // Добавьте аналогичные блоки для других стратегий

        // Кнопка сброса настроек
        buttons.add(List.of(createButton("🔄 Сбросить настройки", BotCallback.RESET_CONFIG.getValue() + ":" + strategyName)));

        return appendBackButton(buttons);
    }

    // Метод для создания меню выбора биржи
    public InlineKeyboardMarkup getExchangeSelectionMenu() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // Перечень бирж, с которыми пользователь может работать
        List<String> exchanges = List.of("BINANCE", "BYBIT", "KUCOIN");

        for (String exchange : exchanges) {
            buttons.add(List.of(createButton("📈 " + exchange, BotCallback.SET_EXCHANGE.getValue() + ":" + exchange)));
        }

        // Добавление кнопки для возврата
        return appendBackButton(buttons);
    }

    public InlineKeyboardMarkup getTimeframeSelectionMenu() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // Список таймфреймов
        List<String> timeframes = List.of("1m", "5m", "15m", "1h", "4h", "1d");

        // Создаём кнопки для каждого таймфрейма
        for (String tf : timeframes) {
            buttons.add(List.of(createButton(tf, BotCallback.SET_TIMEFRAME.getValue() + ":" + tf)));
        }

        // Добавляем кнопку "Назад"
        return appendBackButton(buttons);
    }

    public InlineKeyboardMarkup getSymbolSelectionMenu() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        // Список доступных символов
        List<String> symbols = List.of("BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT");

        // Создаём кнопки для каждого символа
        for (String symbol : symbols) {
            buttons.add(List.of(createButton(symbol, BotCallback.SET_SYMBOL.getValue() + ":" + symbol)));
        }

        // Добавляем кнопку "Назад"
        return appendBackButton(buttons);
    }

    // Кнопка назад
    public InlineKeyboardMarkup appendBackButton(List<List<InlineKeyboardButton>> buttons) {
        buttons.add(List.of(createButton("🔙 Назад", BotCallback.BACK.getValue())));
        return buildKeyboard(buttons);
    }

    public InlineKeyboardMarkup buildKeyboard(List<List<InlineKeyboardButton>> buttons) {
        return InlineKeyboardMarkup.builder().keyboard(buttons).build();
    }

    public InlineKeyboardButton createButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
