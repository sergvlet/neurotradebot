// src/main/java/com/chicu/neurotradebot/trade/service/impl/BinanceAccountService.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.trade.service.binance.BinanceApiClient;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import com.chicu.neurotradebot.trade.service.AccountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Реализация AccountService для Binance.
 */
@Service
@RequiredArgsConstructor
@Primary

public class BinanceAccountService implements AccountService {

    private final BinanceClientProvider clientProvider;
    private final ObjectMapper objectMapper;

    @Override
    public BigDecimal getFreeBalance(Long userId, String asset) {
        try {
            BinanceApiClient client = clientProvider.getClientForUser(userId);
            String json = client.getAccountInfo();  // JSON из Binance
            JsonNode root = objectMapper.readTree(json);
            for (JsonNode b : root.path("balances")) {
                if (asset.equals(b.path("asset").asText())) {
                    return new BigDecimal(b.path("free").asText());
                }
            }
        } catch (Exception e) {
            // логируем ошибку, но возвращаем 0
        }
        return BigDecimal.ZERO;
    }
}
