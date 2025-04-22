package com.chicu.neurotradebot.ai.strategy.ml;

import java.util.List;

/**
 * Интерфейс предсказания следующей цены.
 */
public interface PricePredictor {
    double predictNextPrice(List<Double> history);
}
