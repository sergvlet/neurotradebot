// src/main/java/com/chicu/neurotradebot/exchange/binance/BinanceClientFactoryImpl.java
package com.chicu.neurotradebot.trade.service.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import org.springframework.stereotype.Component;

@Component
public class BinanceClientFactoryImpl implements BinanceClientFactory {

    private static final String MAINNET_URL = "https://api.binance.com";
    private static final String TESTNET_URL = "https://testnet.binance.vision";

    @Override
    public BinanceApiClient create(String apiKey, String apiSecret, boolean testMode) {
        String baseUrl = testMode ? TESTNET_URL : MAINNET_URL;
        SpotClientImpl spotClient = new SpotClientImpl(apiKey, apiSecret, baseUrl);
        return new BinanceApiClientImpl(spotClient);
    }
}
