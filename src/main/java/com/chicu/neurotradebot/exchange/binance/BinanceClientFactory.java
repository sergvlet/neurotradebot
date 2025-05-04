package com.chicu.neurotradebot.exchange.binance;

public interface BinanceClientFactory {
    BinanceApiClient create(String apiKey, String apiSecret, boolean testMode);
}
