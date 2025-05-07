// src/main/java/com/chicu/neurotradebot/trade/service/MarketDataService.java
package com.chicu.neurotradebot.trade.service;


import com.chicu.neurotradebot.entity.Bar;

import java.time.Duration;
import java.util.List;

/**
 * Сервис для получения исторических OHLCV-баров.
 */
public interface MarketDataService {

    /**
     * Возвращает последние `limit` баров для заданного symbol и interval.
     *
     * @param symbol   тикер (например, "BTCUSDT")
     * @param interval таймфрейм в формате Binance ("1m", "5m" и т.п.)
     * @param limit    максимальное число баров
     * @param chatId  Telegram-чат, от имени которого идёт запрос
     * @return список баров в хронологическом порядке
     */
    List<Bar> getHistoricalBars(String symbol, Duration interval, int limit, Long chatId);
}
