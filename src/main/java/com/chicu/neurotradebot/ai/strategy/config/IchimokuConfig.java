package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IchimokuConfig {
    private int conversionLinePeriod = 9;   // Tenkan-sen
    private int baseLinePeriod = 26;        // Kijun-sen
    private int laggingSpanPeriod = 52;     // Senkou Span B
}
