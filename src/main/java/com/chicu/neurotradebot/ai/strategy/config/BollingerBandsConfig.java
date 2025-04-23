package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация для стратегии Bollinger Bands
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BollingerBandsConfig implements StrategyConfig {

    /**
     * Период расчета средней цены (по умолчанию 20)
     */
    private int period = 20;

    /**
     * Множитель стандартного отклонения (по умолчанию 2.0)
     */
    private double multiplier = 2.0;
}
