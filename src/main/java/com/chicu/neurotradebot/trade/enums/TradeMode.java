package com.chicu.neurotradebot.trade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TradeMode {
    DEMO("Демо режим"),
    REAL("Реальный режим");

    private final String title;

    public boolean isDemo() {
        return this == DEMO;
    }

    public boolean isReal() {
        return this == REAL;
    }
}
