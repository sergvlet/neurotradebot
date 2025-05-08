// src/main/java/com/chicu/neurotradebot/trade/service/impl/MarketDataServiceImpl.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.trade.service.MarketDataService;
import com.chicu.neurotradebot.trade.service.binance.BinanceApiClient;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class MarketDataServiceImpl implements MarketDataService {

    private final BinanceClientProvider clientProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Bar> getHistoricalBars(String symbol,
                                       Duration interval,
                                       int limit,
                                       Long chatId) {
        BinanceApiClient client = clientProvider.getClientForUser(chatId);

        // конвертируем Duration → Binance interval string
        String binanceInterval = toBinanceInterval(interval);

        // получаем JSON
        String rawJson = client.getKlines(symbol, binanceInterval, limit);

        // парсим в List<List<Object>>
        List<List<Object>> raw;
        try {
            raw = objectMapper.readValue(
                rawJson,
                new TypeReference<List<List<Object>>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Не удалось распарсить klines JSON", e);
        }

        // строим Bar
        return raw.stream()
            .map(cols -> Bar.builder()
                .openTime(((Number) cols.get(0)).longValue())
                .open(new BigDecimal(cols.get(1).toString()))
                .high(new BigDecimal(cols.get(2).toString()))
                .low(new BigDecimal(cols.get(3).toString()))
                .close(new BigDecimal(cols.get(4).toString()))
                .volume(new BigDecimal(cols.get(5).toString()))
                .build()
            )
            .collect(Collectors.toList());
    }

    /**
     * Преобразует java.time.Duration в строку-интервал Binance.
     * Поддерживаются минуты (m) и часы (h) и дни (d).
     */
    private String toBinanceInterval(Duration interval) {
        long seconds = interval.getSeconds();
        if (seconds % 86400 == 0) {
            long days = seconds / 86400;
            return days + "d";
        }
        if (seconds % 3600 == 0) {
            long hours = seconds / 3600;
            return hours + "h";
        }
        if (seconds % 60 == 0) {
            long minutes = seconds / 60;
            return minutes + "m";
        }
        throw new IllegalArgumentException("Неподдерживаемый интервал: " + interval);
    }
}
