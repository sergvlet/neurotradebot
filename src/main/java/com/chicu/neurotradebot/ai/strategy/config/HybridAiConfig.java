package com.chicu.neurotradebot.ai.strategy.config;

import lombok.Data;
import java.util.List;

@Data
public class HybridAiConfig {

    /**
     * Какие стратегии участвуют в гибридной.
     */
    private List<String> strategyNames;

    /**
     * Какой порог необходим для BUY/SELL решения (например, > 50% сигналов).
     */
    private double threshold = 0.5;
}
