package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HybridAiConfig implements  StrategyConfig {

    /**
     * Какие стратегии участвуют в гибридной.
     */
    private List<String> strategyNames;

    /**
     * Какой порог необходим для BUY/SELL решения (например, > 50% сигналов).
     */
    private double threshold = 0.5;
}
