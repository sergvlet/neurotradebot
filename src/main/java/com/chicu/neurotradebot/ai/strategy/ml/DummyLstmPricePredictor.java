package com.chicu.neurotradebot.ai.strategy.ml;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Заглушка для LSTM-модели.
 */
@Component
public class DummyLstmPricePredictor implements PricePredictor {

    @Override
    public double predictNextPrice(List<Double> history) {
        // Простая логика: возвращаем последнее значение (заглушка)
        return history.get(history.size() - 1);
    }
}
