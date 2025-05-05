// src/main/java/com/chicu/neurotradebot/entity/RsiMacdConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

/**
 * Параметры стратегии RSI+MACD:
 * периоды расчёта и уровни перекупленности/перепроданности.
 */
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RsiMacdConfig {
    /** Период для расчёта RSI */
    private Integer rsiPeriod   = 14;
    /** Уровень перепроданности (сигнал BUY) */
    private BigDecimal rsiLower = BigDecimal.valueOf(30);
    /** Уровень перекупленности (сигнал SELL) */
    private BigDecimal rsiUpper = BigDecimal.valueOf(70);

    /** Быстрый EMA для MACD */
    private Integer macdFast    = 12;
    /** Медленный EMA для MACD */
    private Integer macdSlow    = 26;
    /** Период сигнальной EMA для MACD */
    private Integer macdSignal  = 9;

    /** Сброс параметров к значениям по умолчанию */
    public void resetToDefaults() {
        this.rsiPeriod  = 14;
        this.rsiLower   = BigDecimal.valueOf(30);
        this.rsiUpper   = BigDecimal.valueOf(70);
        this.macdFast   = 12;
        this.macdSlow   = 26;
        this.macdSignal = 9;
    }
}
