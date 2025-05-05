package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.enums.Bar;

import java.util.List;

public interface MarketDataService {

    /**
     * Возвращает исторические OHLCV бары.
     *
     * @param symbol   тикер (например, "BTCUSDT")
     * @param interval таймфрейм в формате Binance ("1m", "5m" и т.п.)
     * @param limit    число последних баров
     */
    List<Bar> getHistoricalBars(String symbol, String interval, int limit);
}
