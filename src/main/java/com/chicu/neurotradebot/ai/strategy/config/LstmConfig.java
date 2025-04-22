package com.chicu.neurotradebot.ai.strategy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конфигурация для LSTM-прогнозирования.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LstmConfig {
    private int windowSize = 30; // количество свечей для анализа
    private int forecastSize = 1; // на сколько свечей прогноз
}
