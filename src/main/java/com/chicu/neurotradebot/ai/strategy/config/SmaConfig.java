package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmaConfig implements  StrategyConfig {
    private int shortPeriod = 5;
    private int longPeriod = 20;
}
