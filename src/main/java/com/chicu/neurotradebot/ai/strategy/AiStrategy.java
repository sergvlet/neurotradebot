package com.chicu.neurotradebot.ai.strategy;

import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;


import java.util.List;

/**
 * Базовый интерфейс для всех AI-стратегий
 */
public interface AiStrategy {

    /**
     * Название стратегии (для отображения)
     */
    String getName();

    /**
     * Вычислить торговый сигнал на основе свечей
     *
     * @param candles список свечей (OHLCV)
     * @return BUY / SELL / HOLD
     */
    Signal analyze(List<MarketCandle> candles);
}
