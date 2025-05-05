// src/main/java/com/chicu/neurotradebot/entity/RiskConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

/**
 * Параметры управления рисками:
 * процент от баланса, стоп-лосс, тейк-профит, максимальные позиции.
 */
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RiskConfig {
    /** Максимальный % от депозита на одну сделку */
    private BigDecimal maxPercentPerTrade = BigDecimal.valueOf(1);
    /** Стоп-лосс в % от цены входа */
    private BigDecimal stopLossPercent     = BigDecimal.valueOf(1);
    /** Тейк-профит в % от цены входа */
    private BigDecimal takeProfitPercent   = BigDecimal.valueOf(2);
    /** Максимальное число одновременных позиций */
    private Integer maxOpenPositions       = 5;

    /** Сброс параметров к значениям по умолчанию */
    public void resetToDefaults() {
        this.maxPercentPerTrade = BigDecimal.valueOf(1);
        this.stopLossPercent    = BigDecimal.valueOf(1);
        this.takeProfitPercent  = BigDecimal.valueOf(2);
        this.maxOpenPositions   = 5;
    }
}
