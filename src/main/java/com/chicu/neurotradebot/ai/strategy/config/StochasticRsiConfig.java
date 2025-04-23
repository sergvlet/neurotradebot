package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StochasticRsiConfig implements  StrategyConfig {
    private int rsiPeriod = 14;
    private int stochasticPeriod = 14;
    private int kPeriod = 3;
    private int dPeriod = 3;
}
