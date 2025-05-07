// src/main/java/com/chicu/neurotradebot/exchange/binance/BinanceClientFactory.java
package com.chicu.neurotradebot.trade.service.binance;

/**
 * Фабрика SpotClientImpl для Binance.
 */
public interface BinanceClientFactory {
    /**
     * @param apiKey    API Key
     * @param apiSecret API Secret
     * @param testMode  true → тестовая сеть, false → основная сеть
     */
    BinanceApiClient create(String apiKey, String apiSecret, boolean testMode);
}
