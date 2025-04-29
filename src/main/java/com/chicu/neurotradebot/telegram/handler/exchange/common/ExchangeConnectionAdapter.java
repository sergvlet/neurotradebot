package com.chicu.neurotradebot.telegram.handler.exchange.common;

public interface ExchangeConnectionAdapter {
    boolean testConnection(String apiKey, String secretKey, boolean useTestnet);
}
