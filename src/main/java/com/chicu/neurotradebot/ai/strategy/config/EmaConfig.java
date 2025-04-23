package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmaConfig implements StrategyConfig {
    private int shortPeriod = 12;
    private int longPeriod = 26;
}
