package com.chicu.neurotradebot.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BotCallback {

    // Главное меню
    MAIN_MENU("main_menu"),

    // Ручная торговля
    TRADE_MODE_MANUAL("trade_mode_manual"),
    MANUAL_MENU("manual_menu"),
    MANUAL_TRADE_EXECUTE("manual_trade_execute"),
    MANUAL_TRADE_STATS("manual_trade_stats"),
    MANUAL_TRADE_HISTORY("manual_trade_history"),
    MANUAL_TRADE_SETTINGS("manual_trade_settings"),
    MANUAL_STRATEGY_MENU("manual_strategy_menu"),
    MANUAL_TRADE_LIMIT("manual_trade_limit"),
    MANUAL_SYMBOL_MENU("manual_symbol_menu"),
    MANUAL_TIMEFRAME_MENU("manual_timeframe_menu"),
    MANUAL_EXCHANGE_MENU("manual_exchange_menu"),


    // Торговля
    START_TRADE("start_trade"),
    TRADE_MODE_AI("trade_mode_ai"),

    SHOW_STRATEGY_GRAPH("show_strategy_graph"),

    // Статистика и история
    STATISTICS("statistics"),
    HISTORY("history"),

    // Настройки
    SETTINGS("settings"),
    STRATEGY_MENU("strategy_menu"),
    TOGGLE_STRATEGY("toggle_strategy"),
    SELECT_MODE("select_mode"),
    TOGGLE_MODE("toggle_mode"),
    TRADE_LIMIT("trade_limit"),
    SET_TRADE_LIMIT("set_trade_limit"),

    // Выбор символа и таймфрейма
    SYMBOL_MENU("symbol_menu"),
    SET_SYMBOL("set_symbol"),
    TIMEFRAME_MENU("timeframe_menu"),
    SET_TIMEFRAME("set_timeframe"),

    // Выбор биржи
    EXCHANGE_MENU("exchange_menu"),
    SET_EXCHANGE("set_exchange"),

    // Ввод API ключей
    ENTER_API_KEYS("enter_api_keys"),

    // Подписка
    SUBSCRIBE("subscribe"),

    // Информация
    BOT_INFO("bot_info"),

    // Бинанс
    SHOW_BALANCE("show_balance"),
    SET_TRADE_TYPE("set_trade_type"),
    // Навигация
    BACK("back");

    private final String value;

    public static BotCallback fromValue(String value) {
        for (BotCallback callback : values()) {
            if (callback.getValue().equalsIgnoreCase(value)) {
                return callback;
            }
        }
        throw new IllegalArgumentException("Unknown callback: " + value);
    }
}
