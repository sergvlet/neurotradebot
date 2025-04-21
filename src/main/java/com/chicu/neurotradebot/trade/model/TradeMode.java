package com.chicu.neurotradebot.trade.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TradeMode {
    DEMO("Демо режим"),
    REAL("Реальный режим");

    private final String title;
}
