package com.chicu.neurotradebot.binance.model;

public enum OrderSide {
    BUY,   // Покупка
    SELL;  // Продажа

    // Пример метода, который может быть полезен для использования
    public static OrderSide fromString(String side) {
        if (side == null) {
            return null;
        }
        switch (side.toUpperCase()) {
            case "BUY":
                return BUY;
            case "SELL":
                return SELL;
            default:
                throw new IllegalArgumentException("Unknown order side: " + side);
        }
    }
}
