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
    BACK("back");

    private final String value;

    public static BotCallback fromValue(String value) {
        return Arrays.stream(BotCallback.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElse(MAIN_MENU);
    }
}
