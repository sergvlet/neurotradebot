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
public class XgboostConfig {
    private int historySize = 30;
}
