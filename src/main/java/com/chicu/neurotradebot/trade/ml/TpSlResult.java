package com.chicu.neurotradebot.trade.ml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Результат предсказания ML-сервиса: TP и SL в процентах.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TpSlResult {
    /** Процент тейк-профита (например, 1.5 → 1.5%) */
    private double tpPercent;
    /** Процент стоп-лосса (например, 0.8 → 0.8%) */
    private double slPercent;
}
