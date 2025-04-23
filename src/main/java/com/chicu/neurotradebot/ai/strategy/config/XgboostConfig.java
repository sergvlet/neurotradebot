package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация XGBoost стратегии.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XgboostConfig implements  StrategyConfig {
    private int historySize = 30;
}
