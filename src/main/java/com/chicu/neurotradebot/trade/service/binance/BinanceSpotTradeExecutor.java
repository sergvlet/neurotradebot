// src/main/java/com/chicu/neurotradebot/trade/service/binance/BinanceSpotTradeExecutor.java
package com.chicu.neurotradebot.trade.service.binance;

import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceSpotTradeExecutor implements SpotTradeExecutor {

    private final BinanceClientProvider clientProvider;

    @Override
    public void buy(Long userId, String symbol, BigDecimal quantity) {
        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            client.newOrder(symbol, "BUY", quantity);
        } catch (IllegalStateException ex) {
            log.warn("Не удалось разместить BUY-ордер для {} (userId={}): {}", symbol, userId, ex.getMessage());
        } catch (Exception ex) {
            log.error("Ошибка при попытке BUY для {} (userId={}): {}", symbol, userId, ex.toString());
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
            log.error("Ошибка при попытке SELL для {} (userId={}): {}", symbol, userId, ex.toString());
        }
    }
}
