package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CciConfig {
    private int period = 20;
    private double overboughtThreshold = 100.0;
    private double oversoldThreshold = -100.0;
}
