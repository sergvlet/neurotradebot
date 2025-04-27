package com.chicu.neurotradebot.exchange.core;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeClient {

    BigDecimal getBalance(String asset, Long userId);

    String placeBuyOrder(String symbol, BigDecimal quantity, Long userId);

    String placeSellOrder(String symbol, BigDecimal quantity, Long userId);

    boolean testConnection(Long userId);

    Map<String, BigDecimal> getAllBalances(Long userId);
}
