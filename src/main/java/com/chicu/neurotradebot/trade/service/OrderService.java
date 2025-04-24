package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.binance.BinanceApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("tradeOrderService")
@RequiredArgsConstructor
public class OrderService {

    private final BinanceApiClient binanceApiClient;

    /**
     * Размещение ордера на покупку
     */
    public String placeBuyOrder(Long chatId, String symbol, double qty, boolean useRealKeys) {
        // Передаем все параметры: chatId, symbol, side (BUY), qty, useRealKeys
        return binanceApiClient.placeOrder(chatId, symbol, "BUY", qty, useRealKeys);
    }

    /**
     * Размещение ордера на продажу
     */
    public String placeSellOrder(Long chatId, String symbol, double qty, boolean useRealKeys) {
        // Передаем все параметры: chatId, symbol, side (SELL), qty, useRealKeys
        return binanceApiClient.placeOrder(chatId, symbol, "SELL", qty, useRealKeys);
    }
}
