// src/main/java/com/chicu/neurotradebot/trade/service/impl/BinanceMarketDataService.java
package com.chicu.neurotradebot.trade.service.impl;


import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.trade.service.MarketDataService;
import com.chicu.neurotradebot.exchange.binance.BinanceClientProvider;
import com.chicu.neurotradebot.exchange.binance.BinanceApiClient;
import com.chicu.neurotradebot.telegram.BotContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация MarketDataService для Binance.
 * Использует BinanceApiClientProvider для получения клиентa,
 * затем запрашивает исторические свечи (OHLCV) и преобразует их
 * в ваш enum Bar.
 */
@Service
@RequiredArgsConstructor
public class BinanceMarketDataService implements MarketDataService {

    private final BinanceClientProvider clientProvider;
    private final ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bar> getHistoricalBars(String symbol, String interval, int limit) {
        // Берём client для текущего пользователя из BotContext
        Long userId = BotContext.getChatId();
        BinanceApiClient client = clientProvider.getClientForUser(userId);

        // Выполняем запрос к Binance (Klines)
        String rawJson = client.getKlines(symbol, interval, limit);

        // Парсим ответ и собираем список Bar
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            List<Bar> bars = new ArrayList<>(root.size());
            for (JsonNode candle : root) {
                // candle — массив [ openTime, open, high, low, close, volume, ... ]
                Bar b = Bar.builder()
                    .openTime(candle.get(0).asLong())
                    .open(candle.get(1).decimalValue())
                    .high(candle.get(2).decimalValue())
                    .low(candle.get(3).decimalValue())
                    .close(candle.get(4).decimalValue())
                    .volume(candle.get(5).decimalValue())
                    .build();
                bars.add(b);
            }
            return bars;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить историю баров для " 
                + symbol + " / " + interval, e);
        }
    }
}
