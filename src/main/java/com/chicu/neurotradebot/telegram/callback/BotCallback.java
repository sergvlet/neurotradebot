package com.chicu.neurotradebot.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum BotCallback {
    MAIN_MENU("main_menu"),
    START_TRADE("start_trade"),
    SETTINGS("settings"),
    SUBSCRIBE("subscribe"),
    BOT_INFO("bot_info"),
    STRATEGY_MENU("strategy_menu"),
    TOGGLE_STRATEGY("toggle_strategy"),
    SELECT_MODE("select_mode"),
    TOGGLE_MODE("toggle_mode"),
    TRADE_LIMIT("trade_limit"),

    BACK("back");

    private final String value;

    public static BotCallback fromValue(String value) {
        return Arrays.stream(BotCallback.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElse(MAIN_MENU);
    }
}
