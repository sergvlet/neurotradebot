package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.model.Candle;
import com.chicu.neurotradebot.model.UserTradingSettings;
import com.chicu.neurotradebot.repository.UserTradingSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceCandleService {

    private final UserTradingSettingsRepository tradingSettingsRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String REAL_BASE_URL = "https://api.binance.com/api/v3/klines";
    private static final String TEST_BASE_URL = "https://testnet.binance.vision/api/v3/klines";

    public List<Candle> getRecentCandles(String symbol, String interval, int limit, boolean isTestnet) {
        String baseUrl = isTestnet ? TEST_BASE_URL : REAL_BASE_URL;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("symbol", symbol.replace("/", "")) // важно: формат BTCUSDT
                .queryParam("interval", interval)
                .queryParam("limit", limit);

        String url = builder.toUriString();

        List<List<Object>> raw = restTemplate.getForObject(url, List.class); // безопасный тип
        List<Candle> candles = new ArrayList<>();
        if (raw == null) return candles;

        for (List<Object> kline : raw) {
            long openTime = ((Number) kline.get(0)).longValue();
            ZonedDateTime time = Instant.ofEpochMilli(openTime).atZone(ZoneOffset.UTC);

            Candle candle = new Candle();
            candle.setTime(time);
            candle.setOpen(Double.parseDouble(kline.get(1).toString()));
            candle.setHigh(Double.parseDouble(kline.get(2).toString()));
            candle.setLow(Double.parseDouble(kline.get(3).toString()));
            candle.setClose(Double.parseDouble(kline.get(4).toString()));
            candle.setVolume(Double.parseDouble(kline.get(5).toString()));
            candle.setQuoteVolume(Double.parseDouble(kline.get(7).toString()));
            candle.setTradeCount(((Number) kline.get(8)).longValue());
            candle.setTakerBuyBaseVolume(Double.parseDouble(kline.get(9).toString()));
            candle.setTakerBuyQuoteVolume(Double.parseDouble(kline.get(10).toString()));

            candles.add(candle);
        }

        return candles;
    }
}
