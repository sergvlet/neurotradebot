package com.chicu.neurotradebot.repository;

public interface ExchangeConnectionAdapter {
    boolean testConnection(String apiKey, String secretKey, boolean useTestnet);
}
