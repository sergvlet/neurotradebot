package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация для стратегии Donchian Channel.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonchianChannelConfig {
    private int period = 20;
}
