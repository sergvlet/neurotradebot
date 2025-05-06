package com.chicu.neurotradebot.exchange.binance;

import com.binance.connector.client.exceptions.BinanceClientException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public interface BinanceApiClient {
    void testConnectivity() throws BinanceClientException;
    String getAccountInfo() throws BinanceClientException;

    /**
     * Общий метод выставления ордера с любыми параметрами.
     */
    String newOrder(Map<String, Object> params) throws BinanceClientException;

    /**
     * Удобный метод для рыночного ордера: выставляет MARKET BUY/SELL по объёму.
     */
    default String newOrder(String symbol, String side, BigDecimal quantity) throws BinanceClientException {
        Map<String,Object> params = new HashMap<>();
        params.put("symbol", symbol);
        params.put("side", side);
        params.put("type", "MARKET");
        params.put("quantity", quantity.toPlainString());
        return newOrder(params);
    }
    /**
     * Исторические свечи (Klines).
     *
     * @param symbol   например "BTCUSDT"
     * @param interval например "1m", "5m"
     * @param limit    сколько последних баров вернуть
     * @return JSON-строка, как возвращает Binance API
     */
    String getKlines(String symbol, String interval, int limit) throws BinanceClientException;

    /** Информация по всем инструментам биржи (exchangeInfo) */
    String getExchangeInfo() throws BinanceClientException;

    /** 24h тикеры по всем символам (ticker24H) */
    String get24hrTicker() throws BinanceClientException;

}
