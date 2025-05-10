// src/main/java/com/chicu/neurotradebot/trade/service/impl/SpotTradeExecutorImpl.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import com.chicu.neurotradebot.trade.service.binance.BinanceApiClient;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpotTradeExecutorImpl implements SpotTradeExecutor {

    private final BinanceClientProvider clientProvider;

    @Override
    public void buy(Long userId, String symbol, BigDecimal quantity) {
        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            client.newOrder(Map.of(
                "symbol",   symbol,
                "side",     "BUY",
                "type",     "MARKET",
                "quantity", quantity
            ));
            log.info("BUY market order placed: {} {} (userId={})", symbol, quantity, userId);
        } catch (Exception ex) {
            log.error("Error placing BUY {} {} (userId={}): {}", symbol, quantity, userId, ex.getMessage());
        }
    }

    @Override
    public void sell(Long userId, String symbol, BigDecimal quantity) {
        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            client.newOrder(Map.of(
                "symbol",   symbol,
                "side",     "SELL",
                "type",     "MARKET",
                "quantity", quantity
            ));
            log.info("SELL market order placed: {} {} (userId={})", symbol, quantity, userId);
        } catch (Exception ex) {
            log.error("Error placing SELL {} {} (userId={}): {}", symbol, quantity, userId, ex.getMessage());
        }
    }

    @Override
    public void placeBracketOrder(Long userId,
                                  String symbol,
                                  BigDecimal quantity,
                                  BigDecimal tpPrice,
                                  BigDecimal slPrice) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Skipping bracket order for {}: invalid qty={} (userId={})", symbol, quantity, userId);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("symbol",           symbol);
        params.put("side",             "BUY");             // или "SELL" при шорте
        params.put("type",             "OCO");
        params.put("quantity",         quantity);
        params.put("price",            tpPrice);           // лимит-цена TakeProfit
        params.put("stopPrice",        slPrice);           // цена активации StopLoss
        params.put("stopLimitPrice",   slPrice);           // лимит для StopLimit
        params.put("stopLimitTimeInForce", "GTC");

        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            client.newOrder(params);
            log.info("Bracket (OCO) order placed: {} qty={} TP@{} SL@{} (userId={})",
                     symbol, quantity, tpPrice, slPrice, userId);
        } catch (Exception ex) {
            log.error("Error placing bracket order {} qty={} (userId={}): {}",
                      symbol, quantity, userId, ex.getMessage());
        }
    }
}
