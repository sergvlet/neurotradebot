package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.XgboostConfig;
import com.chicu.neurotradebot.ai.strategy.ml.SignalClassifier;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class XgboostSignalStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final SignalClassifier classifier;
    private final OrderService orderService; // Для размещения ордера
    private final TelegramNotificationService notificationService; // Для уведомлений
    private XgboostConfig config = new XgboostConfig();

    @Override
    public String getName() {
        return "XGBoost Signal";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getHistorySize()) {
            log.warn("📉 Недостаточно данных для XGBoost стратегии");
            return Signal.HOLD;
        }

        List<Double> history = new ArrayList<>();
        for (int i = candles.size() - config.getHistorySize(); i < candles.size(); i++) {
            history.add(candles.get(i).getClose());
        }

        // Классификация с использованием XGBoost
        Signal signal = classifier.classify(history);
        log.info("🧠 XGBoost сигнал: {}", signal);
        return signal;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof XgboostConfig xgboostConfig) {
            this.config = xgboostConfig;
        } else {
            log.warn("❌ Неверная конфигурация для XGBoost стратегии: {}", config);
        }
    }

    @Override
    public void execute() {
        // Получаем последние данные для анализа (например, 100 свечей)
        List<MarketCandle> latestCandles = candleService.getLatestCandles("BTCUSDT", "1h", 100); // Пример
        // Выполняем анализ и получаем решение
        Signal decision = analyze(latestCandles);

        // Логика для выполнения ордера в зависимости от сигнала
        if (decision == Signal.BUY) {
            log.info("💡 Сигнал на покупку. Размещение ордера...");
            // Отправляем ордер на покупку
            orderService.placeBuyOrder(123L, "BTCUSDT", 0.01, true);  // Пример
            // Уведомляем пользователя
            notificationService.sendTradeNotification(123L, Signal.BUY);
        } else if (decision == Signal.SELL) {
            log.info("💡 Сигнал на продажу. Размещение ордера...");
            // Отправляем ордер на продажу
            orderService.placeSellOrder(123L, "BTCUSDT", 0.01, true);  // Пример
            // Уведомляем пользователя
            notificationService.sendTradeNotification(123L, Signal.SELL);
        } else {
            log.info("⚪️ Сигнал на удержание. Сделка не размещена.");
        }
    }
}
