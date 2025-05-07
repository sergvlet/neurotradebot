// src/main/java/com/chicu/neurotradebot/trade/service/impl/BinanceMarketDataService.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.trade.service.MarketDataService;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import com.chicu.neurotradebot.trade.service.binance.BinanceApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceMarketDataService implements MarketDataService {

    private final BinanceClientProvider clientProvider;
    private final ObjectMapper objectMapper;

    @Override
    public List<Bar> getHistoricalBars(String symbol, Duration interval, int limit, Long chatId) {
        BinanceApiClient client = clientProvider.getClientForUser(chatId);

        // Преобразуем Duration в формат Binance: "1m", "15m", "1h", "1d" и т.п.
        String binanceInterval = toBinanceInterval(interval);

        String rawJson = client.getKlines(symbol, binanceInterval, limit);

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            List<Bar> bars = new ArrayList<>(root.size());
            for (JsonNode candle : root) {
                bars.add(Bar.builder()
                        .openTime(candle.get(0).asLong())
                        .open(candle.get(1).decimalValue())
                        .high(candle.get(2).decimalValue())
                        .low(candle.get(3).decimalValue())
                        .close(candle.get(4).decimalValue())
                        .volume(candle.get(5).decimalValue())
                        .build());
            }
            return bars;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить историю баров для "
                    + symbol + " / " + binanceInterval, e);
        }
    }

    private String toBinanceInterval(Duration d) {
        long seconds = d.getSeconds();

        if (seconds % 86400 == 0) {            // дни
            long days = seconds / 86400;
            return days + "d";
        } else if (seconds % 3600 == 0) {      // часы
            long hours = seconds / 3600;
            return hours + "h";
        } else if (seconds % 60 == 0) {        // минуты
            long minutes = seconds / 60;
            return minutes + "m";
        } else {                               // секунды (необычно)
            return seconds + "s";
        }
    }
}
