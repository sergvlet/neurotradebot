package com.chicu.neurotradebot.exchange.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.exceptions.BinanceClientException;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BinanceApiClientImpl implements BinanceApiClient {

    private final SpotClientImpl client;

    @Override
    public void testConnectivity() throws BinanceClientException {
        client.createMarket().ping();
    }

    @Override
    public String getAccountInfo() throws BinanceClientException {
        Map<String, Object> params = new HashMap<>();
        return client.createTrade().account(params);  // вот корректный вызов
    }
}
