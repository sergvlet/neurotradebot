package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация для стратегии OBV.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObvConfig implements  StrategyConfig {

    /**
     * Минимальное количество свечей для анализа.
     */
    private int minCandles = 30;

    /**
     * Количество последних значений OBV, которые сравниваются.
     */
    private int lookbackPeriod = 3;
}
