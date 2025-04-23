package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация для стратегии VWAP.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VwapConfig implements  StrategyConfig {
    private int period = 14;
}
