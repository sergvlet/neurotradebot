package com.chicu.neurotradebot.exchange.binance.service;

import com.chicu.neurotradebot.exchange.binance.client.BinanceApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BinanceOrderService {

    private final BinanceApiClient binanceApiClient;

    public BigDecimal getBalance(String asset, Long userId) {
        return binanceApiClient.getBalance(asset, userId);
    }

    public String buy(String symbol, BigDecimal quantity, Long userId) {
        return binanceApiClient.placeBuyOrder(symbol, quantity, userId);
    }

    public String sell(String symbol, BigDecimal quantity, Long userId) {
        return binanceApiClient.placeSellOrder(symbol, quantity, userId);
    }

    public boolean testConnection(Long userId) {
        return binanceApiClient.testConnection(userId);
    }
}
