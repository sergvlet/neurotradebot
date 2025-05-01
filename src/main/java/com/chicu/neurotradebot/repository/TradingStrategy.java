package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.Candle;

import java.util.List;

public interface TradingStrategy {
    boolean shouldBuy(List<Candle> candles);
    boolean shouldSell(List<Candle> candles);
    String getName();
}
