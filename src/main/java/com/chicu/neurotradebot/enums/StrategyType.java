// src/main/java/com/chicu/neurotradebot/enums/StrategyType.java
package com.chicu.neurotradebot.enums;

/**
 * Типы торговых стратегий.
 */
public enum StrategyType {
    RSI_MACD("RSI+MACD 📊"),
    EMA_CROSSOVER("EMA Crossover 🔄"),
    GRID("Grid Trading 📐"),
    DCA("DCA 💰"),
    SCALPING("Scalping ⚡"),
    COMBINED_INDICATORS("Combined Indicators 🔀");

    /** Отображаемое имя стратегии в меню */
    private final String displayName;

    StrategyType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return Человекочитаемое имя стратегии с эмодзи
     */
    public String getDisplayName() {
        return displayName;
    }
}
