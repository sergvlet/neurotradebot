package com.chicu.neurotradebot.telegram.handler.exchange.common;

import com.chicu.neurotradebot.telegram.handler.exchange.binance.BinanceConnectionAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeConnectionService {

    private final BinanceConnectionAdapter binanceConnectionAdapter;

    public boolean testConnection(String exchange, String apiKey, String secretKey, boolean useTestnet) {
        if ("BINANCE".equalsIgnoreCase(exchange)) {
            return binanceConnectionAdapter.testConnection(apiKey, secretKey, useTestnet);
        }

        // Можно расширить под другие биржи в будущем
        throw new UnsupportedOperationException("Биржа не поддерживается: " + exchange);
    }
}
