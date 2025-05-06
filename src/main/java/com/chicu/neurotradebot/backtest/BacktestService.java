// src/main/java/com/chicu/neurotradebot/backtest/BacktestService.java
package com.chicu.neurotradebot.backtest;

import com.chicu.neurotradebot.entity.AiTradeSettings;

public interface BacktestService {
    BacktestResult runBacktest(AiTradeSettings settings) throws Exception;

    // где Point — простая запись:
    public record Point(long timestamp, double equity) {}
}
