package com.chicu.neurotradebot.trade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TradeType {
    AI("🤖 AI-торговля"),
    MANUAL("✋ Ручная торговля");

    private final String title;
}
