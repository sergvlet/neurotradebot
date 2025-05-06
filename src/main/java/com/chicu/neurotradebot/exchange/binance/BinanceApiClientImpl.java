// src/main/java/com/chicu/neurotradebot/exchange/binance/BinanceApiClientImpl.java
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
        return client.createTrade().account(params);
    }

    @Override
    public String newOrder(Map<String, Object> params) throws BinanceClientException {
        return client.createTrade().newOrder(params);
    }

    @Override
    public String getKlines(String symbol, String interval, int limit) throws BinanceClientException {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("interval", interval);
        params.put("limit", limit);
        return client.createMarket().klines(params);
    }

    @Override
    public String getExchangeInfo() throws BinanceClientException {
        // Берём информацию о бирже через market API
        return client.createMarket().exchangeInfo(new HashMap<>());
    }

    @Override
    public String get24hrTicker() throws BinanceClientException {
        // Берём 24h статистику по всем рынкам через market API
        return client.createMarket().ticker24H(new HashMap<>());
    }

}
