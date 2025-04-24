package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;

import java.util.List;

public interface StrategyRunnerService {
    Signal analyzeSignal(Long chatId, List<MarketCandle> candles);
    void executeTrade(Long chatId, Signal signal);
}
