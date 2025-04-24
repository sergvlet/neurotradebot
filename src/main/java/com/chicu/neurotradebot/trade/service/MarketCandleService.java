package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketCandleService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MarketCandle> getCandles(String symbol, String interval) {
        List<MarketCandle> candles = new ArrayList<>();
        try {
            long from = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L; // последние 7 дней
            String urlStr = String.format(
                    "https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d",
                    symbol.toUpperCase(), interval, from
            );

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            JsonNode root = objectMapper.readTree(json.toString());
            for (JsonNode node : root) {
                long timestamp = node.get(0).asLong();
                double open = node.get(1).asDouble();
                double high = node.get(2).asDouble();
                double low = node.get(3).asDouble();
                double close = node.get(4).asDouble();
                double volume = node.get(5).asDouble();

                candles.add(MarketCandle.builder()
                        .timestamp(Instant.ofEpochMilli(timestamp)) // Установили openTime в Instant
                        .open(open)
                        .high(high)
                        .low(low)
                        .close(close)
                        .volume(volume)
                        .closeTime(Instant.ofEpochMilli(timestamp).plus(Duration.ofMinutes(1))) // Пример с закрытием свечи через 1 минуту
                        .build());
            }

        } catch (Exception e) {
            log.error("❌ Ошибка при получении свечей с Binance", e);
        }

        return candles;
    }

    /**
     * Построить BarSeries для анализа стратегии (SMA, RSI и т.д.)
     */
    public BarSeries buildBarSeries(List<MarketCandle> candles) {
        BarSeries series = new BaseBarSeries("binance_series");

        for (MarketCandle candle : candles) {
            Duration barDuration = Duration.ofMinutes(1); // можно позже сделать гибко
            series.addBar(new BaseBar(
                    barDuration,
                    ZonedDateTime.ofInstant(candle.getTimestamp(), ZoneId.systemDefault()), // Преобразование в ZonedDateTime
                    String.valueOf(candle.getOpen()),
                    String.valueOf(candle.getHigh()),
                    String.valueOf(candle.getLow()),
                    String.valueOf(candle.getClose()),
                    String.valueOf(candle.getVolume())
            ));
        }

        return series;
    }

    /**
     * Удобный метод-заглушка для получения свечей (для совместимости со стратегиями)
     */
    public List<MarketCandle> getLatestCandles(String symbol, String interval, int i) {
        return getCandles(symbol, interval);
    }
}
