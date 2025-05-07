// src/main/java/com/chicu/neurotradebot/trade/service/impl/AccountServiceImpl.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.trade.service.AccountService;
import com.chicu.neurotradebot.trade.service.binance.BinanceApiClient;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Реализация AccountService для Binance.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final BinanceClientProvider clientProvider;
    private final ObjectMapper        objectMapper;

    /**
     * Возвращает свободный (free) баланс запрошенного актива.
     */
    @Override
    public BigDecimal getFreeBalance(Long userId, String asset) {
        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            String json = client.getAccountInfo();  // Берём весь JSON с информацией об аккаунте
            JsonNode root = objectMapper.readTree(json);
            for (JsonNode b : root.path("balances")) {
                if (asset.equalsIgnoreCase(b.path("asset").asText())) {
                    return new BigDecimal(b.path("free").asText());
                }
            }
        } catch (Exception e) {
            // При ошибке возвращаем ноль (можно залогировать e)
        }
        return BigDecimal.ZERO;
    }
}
