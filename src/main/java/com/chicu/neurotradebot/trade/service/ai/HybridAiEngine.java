package com.chicu.neurotradebot.trade.service.ai;

import com.chicu.neurotradebot.ai.StrategyEngine;
import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.binance.OrderService;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.MarketStreamService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import com.chicu.neurotradebot.trade.service.TradingStatusService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import com.chicu.neurotradebot.trade.model.UserSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HybridAiEngine {

    private final MarketStreamService marketStreamService;
    private final StrategyEngine strategyEngine;
    private final UserSettingsService userSettingsService;
    private final OrderService orderService;
    private final TelegramNotificationService notificationService;
    private final TradingStatusService tradingStatusService;

    public void runForUser(Long chatId) {
        UserSettings settings = userSettingsService.getOrCreate(chatId);
        String symbol = settings.getExchangeSymbol();
        String interval = settings.getTimeframe();

        log.info("▶️ AI-торговля активирована для chatId={} | symbol={} | interval={}", chatId, symbol, interval);

        marketStreamService.subscribeToCandles(symbol, interval, (MarketCandle candle) -> {
            if (!tradingStatusService.isTradingEnabled(chatId)) {
                log.info("⛔️ Торговля отключена пользователем. Пропуск анализа.");
                return;
            }

            Map<AvailableStrategy, Signal> signals = strategyEngine.analyzeAll(chatId, symbol, interval);
            Signal decision = combine(signals);

            log.info("📊 Сигналы стратегий: {}", signals);
            log.info("⚙️ Финальный сигнал: {}", decision);

            switch (decision) {
                case BUY -> {
                    double qty = 1.0; // Пример: количество для покупки
                    boolean useRealKeys = true; // Или false для тестовых ключей
                    orderService.placeBuyOrder(chatId, symbol, qty, useRealKeys);
                    notificationService.sendTradeNotification(chatId, Signal.BUY); // Отправляем уведомление о покупке
                }
                case SELL -> {
                    double qty = 1.0; // Пример: количество для продажи
                    boolean useRealKeys = true; // Или false для тестовых ключей
                    orderService.placeSellOrder(chatId, symbol, qty, useRealKeys);
                    notificationService.sendTradeNotification(chatId, Signal.SELL); // Отправляем уведомление о продаже
                }
                case HOLD -> {
                    log.info("⏸ HOLD — рынок без сигнала.");
                    notificationService.sendTradeNotification(chatId, Signal.HOLD); // Отправляем уведомление об удержании
                }
            }
        });
    }

    private Signal combine(Map<AvailableStrategy, Signal> signals) {
        long buy = signals.values().stream().filter(s -> s == Signal.BUY).count();
        long sell = signals.values().stream().filter(s -> s == Signal.SELL).count();

        if (buy > sell) return Signal.BUY;
        if (sell > buy) return Signal.SELL;
        return Signal.HOLD;
    }
}
