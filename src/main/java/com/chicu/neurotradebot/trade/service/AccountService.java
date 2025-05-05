// src/main/java/com/chicu/neurotradebot/trade/service/AccountService.java
package com.chicu.neurotradebot.trade.service;

import java.math.BigDecimal;

/**
 * Сервис доступа к балансу пользователя на бирже.
 */
public interface AccountService {
    /**
     * Возвращает свободный (available) баланс по указанному активу
     * в аккаунте пользователя userId.
     *
     * @param userId идентификатор пользователя
     * @param asset  тикер актива, например "USDT" или "BTC"
     */
    BigDecimal getFreeBalance(Long userId, String asset);
}
