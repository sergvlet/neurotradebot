package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация для стратегии ADX.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdxConfig implements StrategyConfig {

    /**
     * Период для расчёта ADX (по умолчанию 14).
     */
    private int period = 14;

    /**
     * Порог силы тренда (по умолчанию 20).
     * Если ADX выше этого значения — тренд считается сильным.
     */
    private double trendStrengthThreshold = 20.0;
}
