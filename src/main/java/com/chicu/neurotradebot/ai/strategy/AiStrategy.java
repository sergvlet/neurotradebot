package com.chicu.neurotradebot.ai.strategy;

import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;

import java.util.List;

public interface AiStrategy {

    String getName();

    Signal analyze(List<MarketCandle> candles);

    void setConfig(Object config);
}
