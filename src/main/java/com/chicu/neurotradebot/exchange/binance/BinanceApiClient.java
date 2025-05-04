package com.chicu.neurotradebot.exchange.binance;

import com.binance.connector.client.exceptions.BinanceClientException;

public interface BinanceApiClient {
    void testConnectivity() throws BinanceClientException;
    String getAccountInfo() throws BinanceClientException;
}
