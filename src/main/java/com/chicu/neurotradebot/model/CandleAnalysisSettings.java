package com.chicu.neurotradebot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandleAnalysisSettings {

    private String symbol = "BTCUSDT";          // Пара по умолчанию
    private String interval = "15m";            // Таймфрейм
    private int limit = 100;                    // Кол-во свечей
    private boolean requireBullish = false;     // Использовать только бычьи свечи
    private double minVolume = 0.0;             // Минимальный объём (фильтр)
    private boolean allowTestnet = false;       // Разрешить testnet (если ручной режим)

    private Long startTimeMs = null;            // Диапазон (опционально)
    private Long endTimeMs = null;

    public void resetToDefault() {
        this.symbol = "BTCUSDT";
        this.interval = "15m";
        this.limit = 100;
        this.requireBullish = false;
        this.minVolume = 0.0;
        this.allowTestnet = false;
        this.startTimeMs = null;
        this.endTimeMs = null;
    }

    public boolean isCustomRangeSet() {
        return startTimeMs != null || endTimeMs != null;
    }
}
