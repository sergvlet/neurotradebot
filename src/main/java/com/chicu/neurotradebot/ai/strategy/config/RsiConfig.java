package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsiConfig {
    private int period = 14;
    private double oversold = 30.0;
    private double overbought = 70.0;
}
