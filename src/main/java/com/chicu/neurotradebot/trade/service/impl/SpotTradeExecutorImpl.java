package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import com.chicu.neurotradebot.trade.service.binance.BinanceApiClient;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpotTradeExecutorImpl implements SpotTradeExecutor {

    private final BinanceClientProvider clientProvider;

    @Override
    public void buy(Long userId, String symbol, BigDecimal quantity) {
        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            client.newOrder(symbol, "BUY", quantity);
        } catch (IllegalStateException ex) {
            log.warn("Не удалось разместить BUY-ордер для {} (userId={}): {}", symbol, userId, ex.getMessage());
        } catch (Exception ex) {
            log.error("Ошибка при размещении BUY-ордера для {} (userId={}): {}", symbol, userId, ex.toString());
        }
    }

    @Override
    public void sell(Long userId, String symbol, BigDecimal quantity) {
        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            client.newOrder(symbol, "SELL", quantity);
        } catch (IllegalStateException ex) {
            log.warn("Не удалось разместить SELL-ордер для {} (userId={}): {}", symbol, userId, ex.getMessage());
        } catch (Exception ex) {
            log.error("Ошибка при размещении SELL-ордера для {} (userId={}): {}", symbol, userId, ex.toString());
        }
    }
}
