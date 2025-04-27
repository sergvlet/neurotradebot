package com.chicu.neurotradebot.subscription.service;

import com.chicu.neurotradebot.exchange.binance.client.BinanceApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BinanceAccountService {

    private final BinanceApiClient binanceApiClient;

    public Map<String, BigDecimal> getAllBalances(Long userId) {
        return binanceApiClient.getAllBalances(userId);
    }
}
