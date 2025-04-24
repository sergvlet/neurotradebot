package com.chicu.neurotradebot.binance;

import com.chicu.neurotradebot.trade.service.UserApiKeysService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BinanceApiClient {

    private final BinanceHttpClient binanceHttpClient;
    private final UserApiKeysService userApiKeysService;

    public BinanceApiClient(BinanceHttpClient binanceHttpClient, UserApiKeysService userApiKeysService) {
        this.binanceHttpClient = binanceHttpClient;
        this.userApiKeysService = userApiKeysService;
    }

    /**
     * Размещение ордера на Binance
     *
     * @param chatId  торговая пара (например, BTCUSDT)
     * @param symbol  символ (например, BTCUSDT)
     * @param side    сторона ордера (BUY/SELL)
     * @param qty     количество
     * @param useRealKeys если true, используются реальные ключи, если false — тестовые
     * @return ответ от Binance
     */
    public String placeOrder(Long chatId, String symbol, String side, double qty, boolean useRealKeys) {
        // Получаем ключи пользователя (реальные или тестовые)
        String apiKey = userApiKeysService.getApiKey(chatId, useRealKeys);
        String secretKey = userApiKeysService.getApiSecret(chatId, useRealKeys);

        // Логика отправки ордера на Binance
        String path = "/api/v3/order";
        String method = "POST";

        Map<String, String> params = Map.of(
                "symbol", symbol,
                "side", side,
                "type", "MARKET",  // Тип ордера (MARKET)
                "quantity", String.valueOf(qty)
        );

        return binanceHttpClient.sendSignedRequest(
                "https://api.binance.com", path, method, apiKey, secretKey, params
        );
    }
}
