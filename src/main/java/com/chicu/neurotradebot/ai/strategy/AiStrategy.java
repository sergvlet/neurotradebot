package com.chicu.neurotradebot.ai.strategy;

import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;

import java.util.List;

public interface AiStrategy {

    // Метод для анализа данных
    Signal analyze(List<MarketCandle> candles);

    // Метод для установки конфигурации стратегии
    void setConfig(Object config);

    // Метод для выполнения стратегии (например, торгового сигнала)
    void execute();

    // Метод для получения имени стратегии
    String getName();
}
