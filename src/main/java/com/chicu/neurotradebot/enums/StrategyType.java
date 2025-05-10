// src/main/java/com/chicu/neurotradebot/enums/StrategyType.java
package com.chicu.neurotradebot.enums;

public enum StrategyType {
    RSI("RSI"),
    MACD("MACD"),
    SMA_CROSSOVER("SMA Crossover"),
    EMA_CROSSOVER("EMA Crossover"),
    BOLLINGER_BANDS("Bollinger Bands"),
    STOCHASTIC_OSCILLATOR("Stochastic"),
    MOMENTUM("Momentum"),
    ATR_TRAILING_STOP("ATR Trailing Stop"),
    MEAN_REVERSION("Mean Reversion"),
    VWAP("VWAP"),
    /** добавлены новые стратегии */
    DCA("DCA"),
    SCALPING("Scalping"),
    /** ML-TP/SL стратегия на основе REST-модели */
    ML_TPSL("ML TP/SL");

    private final String displayName;
    StrategyType(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
