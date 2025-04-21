package com.chicu.neurotradebot.ai.strategy;

import com.chicu.neurotradebot.trade.model.Signal;

public interface IndicatorStrategy {
    Signal analyze(String symbol, String interval);
}
