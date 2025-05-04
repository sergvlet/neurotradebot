package com.chicu.neurotradebot.exchange.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BinanceClientFactoryImpl implements BinanceClientFactory {

    @Override
    public BinanceApiClient create(String apiKey, String apiSecret, boolean testMode) {
        Map<String, Object> config = new HashMap<>();
        config.put("baseUrl", testMode
                ? "https://testnet.binance.vision"
                : "https://api.binance.com");

        SpotClientImpl spotClient = new SpotClientImpl(apiKey, apiSecret, String.valueOf(config));
        return new BinanceApiClientImpl(spotClient);
    }
}
