package com.chicu.neurotradebot.exchange.core;

import com.chicu.neurotradebot.exchange.binance.client.BinanceApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class ExchangeConfiguration {

    private final ExchangeRegistry exchangeRegistry;
    private final BinanceApiClient binanceApiClient;

    @PostConstruct
    public void registerExchanges() {
        exchangeRegistry.registerExchange("BINANCE", binanceApiClient);
        // Позже сюда добавим BYBIT, KUCOIN и другие биржи
    }
}
