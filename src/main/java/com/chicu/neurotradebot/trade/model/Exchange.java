package com.chicu.neurotradebot.trade.model;

public enum Exchange {
    BINANCE("Binance"),
    BYBIT("Bybit"),
    KUCOIN("KuCoin");

    private final String title;

    Exchange(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
