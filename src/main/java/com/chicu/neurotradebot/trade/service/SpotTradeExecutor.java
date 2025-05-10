package com.chicu.neurotradebot.trade.service;

import java.math.BigDecimal;

public interface SpotTradeExecutor {

    /**
     * Классический спотовый ордер на покупку.
     */
    void buy(Long userId, String symbol, BigDecimal quantity);

    /**
     * Классический спотовый ордер на продажу.
     */
    void sell(Long userId, String symbol, BigDecimal quantity);

    /**
     * Выставить спотовый OCO-ордер (bracket-order) по абсолютным ценам TP/SL.
     *
     * @param userId   Telegram-chatId пользователя
     * @param symbol   пара, например "BTCUSDT"
     * @param quantity объём в базовой валюте
     * @param tpPrice  абсолютная цена тейк-профита
     * @param slPrice  абсолютная цена стоп-лосса
     */
    void placeBracketOrder(Long userId, String symbol, BigDecimal quantity, BigDecimal tpPrice, BigDecimal slPrice);
}
