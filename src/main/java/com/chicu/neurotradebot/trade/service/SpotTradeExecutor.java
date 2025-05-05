package com.chicu.neurotradebot.trade.service;

import java.math.BigDecimal;

public interface SpotTradeExecutor {

    /**
     * Выставить ордер на покупку на спотовом рынке от имени пользователя.
     *
     * @param userId   идентификатор пользователя (для получения ключей)
     * @param symbol   тикер пары, например "BTCUSDT"
     * @param quantity объём в базовой валюте
     */
    void buy(Long userId, String symbol, BigDecimal quantity);

    /**
     * Выставить ордер на продажу на спотовом рынке от имени пользователя.
     *
     * @param userId   идентификатор пользователя (для получения ключей)
     * @param symbol   тикер пары, например "BTCUSDT"
     * @param quantity объём в базовой валюте
     */
    void sell(Long userId, String symbol, BigDecimal quantity);
}
