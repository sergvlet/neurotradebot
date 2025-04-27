package com.chicu.neurotradebot.exchange.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExchangeRegistry {

    private final Map<String, ExchangeClient> exchangeClients = new HashMap<>();

    public void registerExchange(String exchangeName, ExchangeClient client) {
        exchangeClients.put(exchangeName.toUpperCase(), client);
    }

    public ExchangeClient getClient(String exchangeName) {
        ExchangeClient client = exchangeClients.get(exchangeName.toUpperCase());
        if (client == null) {
            throw new RuntimeException("Биржа " + exchangeName + " не зарегистрирована.");
        }
        return client;
    }
}
