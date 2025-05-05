// src/main/java/com/chicu/neurotradebot/entity/RsiMacdConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

/**
 * Встраиваемая конфигурация параметров стратегии RSI+MACD:
 * периоды расчёта и уровни перекупленности/перепроданности.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsiMacdConfig {
    /** Период для расчёта RSI */
    @Column(name = "rsi_period", nullable = false)
    @Builder.Default
    private Integer rsiPeriod = 14;

    /** Уровень перепроданности (сигнал BUY) */
    @Column(name = "rsi_lower", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal rsiLower = BigDecimal.valueOf(30);

    /** Уровень перекупленности (сигнал SELL) */
    @Column(name = "rsi_upper", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal rsiUpper = BigDecimal.valueOf(70);

    /** Быстрый EMA для MACD */
    @Column(name = "macd_fast", nullable = false)
    @Builder.Default
    private Integer macdFast = 12;

    /** Медленный EMA для MACD */
    @Column(name = "macd_slow", nullable = false)
    @Builder.Default
    private Integer macdSlow = 26;

    /** Период сигнальной EMA для MACD */
    @Column(name = "macd_signal", nullable = false)
    @Builder.Default
    private Integer macdSignal = 9;

    /** Сброс всех параметров к значениям по умолчанию */
    public void resetToDefaults() {
        this.rsiPeriod  = 14;
        this.rsiLower   = BigDecimal.valueOf(30);
        this.rsiUpper   = BigDecimal.valueOf(70);
        this.macdFast   = 12;
        this.macdSlow   = 26;
        this.macdSignal = 9;
    }
}
