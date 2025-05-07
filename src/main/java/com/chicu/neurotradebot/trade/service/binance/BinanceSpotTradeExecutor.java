package com.chicu.neurotradebot.trade.service.binance;


import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BinanceSpotTradeExecutor implements SpotTradeExecutor {

    private final BinanceClientProvider clientProvider;

    @Override
    public void buy(Long userId, String symbol, BigDecimal quantity) {
        BinanceApiClient client = clientProvider.getClientForUser(userId);
        client.newOrder(symbol, "BUY", quantity);
    }

    @Override
    public void sell(Long userId, String symbol, BigDecimal quantity) {
        BinanceApiClient client = clientProvider.getClientForUser(userId);
        client.newOrder(symbol, "SELL", quantity);
    }
}
