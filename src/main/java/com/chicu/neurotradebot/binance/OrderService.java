package com.chicu.neurotradebot.binance;

import com.chicu.neurotradebot.trade.service.UserSettingsService;
import com.chicu.neurotradebot.trade.model.UserSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("binanceOrderService")
@RequiredArgsConstructor
public class OrderService {

    private final BinanceApiClient binanceApiClient;
    private final UserSettingsService userSettingsService;

    /**
     * Размещение ордера на покупку
     *
     * @param chatId      торговый идентификатор пользователя
     * @param symbol      торговая пара (например, BTCUSDT)
     * @param qty         количество для покупки
     * @param useRealKeys если true, используются реальные ключи, если false — тестовые
     * @return результат ордера
     */
    public String placeBuyOrder(Long chatId, String symbol, double qty, boolean useRealKeys) {
        UserSettings settings = userSettingsService.getOrCreate(chatId);
        double tradeLimit = Double.parseDouble(settings.getTradeLimit());

        // Используем qty вместо tradeLimit для расчета
        String orderResult = binanceApiClient.placeOrder(chatId, symbol, "BUY", qty, useRealKeys);
        return orderResult;
    }

    /**
     * Размещение ордера на продажу
     *
     * @param chatId      торговый идентификатор пользователя
     * @param symbol      торговая пара (например, BTCUSDT)
     * @param qty         количество для продажи
     * @param useRealKeys если true, используются реальные ключи, если false — тестовые
     * @return результат ордера
     */
    public String placeSellOrder(Long chatId, String symbol, double qty, boolean useRealKeys) {
        UserSettings settings = userSettingsService.getOrCreate(chatId);
        double tradeLimit = Double.parseDouble(settings.getTradeLimit());

        // Используем qty вместо tradeLimit для расчета
        String orderResult = binanceApiClient.placeOrder(chatId, symbol, "SELL", qty, useRealKeys);
        return orderResult;
    }
}
