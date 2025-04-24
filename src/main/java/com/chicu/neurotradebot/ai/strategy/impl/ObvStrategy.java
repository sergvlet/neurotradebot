package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.ObvConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObvStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private ObvConfig config = new ObvConfig(); // Конфигурация по умолчанию

    @Override
    public String getName() {
        return "OBV";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getMinCandles()) {
            log.warn("📉 Недостаточно данных для OBV стратегии");
            return Signal.HOLD;  // Если данных недостаточно, возвращаем HOLD
        }

        BarSeries series = candleService.buildBarSeries(candles);
        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        int end = series.getEndIndex();
        if (end < config.getLookbackPeriod()) return Signal.HOLD; // Если недостаточно данных для анализа, возвращаем HOLD

        double currentObv = obv.getValue(end).doubleValue();
        double previousObv = obv.getValue(end - config.getLookbackPeriod()).doubleValue();
        double currentClose = close.getValue(end).doubleValue();
        double previousClose = close.getValue(end - config.getLookbackPeriod()).doubleValue();

        log.info("📊 OBV: current={}, previous={}, closeDiff={}", currentObv, previousObv, currentClose - previousClose);

        if (currentObv > previousObv && currentClose > previousClose) {
            return Signal.BUY;
        } else if (currentObv < previousObv && currentClose < previousClose) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof ObvConfig obvConfig) {
            this.config = obvConfig;
        } else {
            log.warn("❌ Неверная конфигурация для OBV стратегии: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии OBV...");

        // Получаем последние данные для анализа (например, 100 свечей)
        List<MarketCandle> latestCandles = new ArrayList<>();  // Получите последние свечи с вашего сервиса
        // Пример: latestCandles = candleService.getLatestCandles("BTCUSDT", "1h", 100);

        // Выполняем анализ и получаем решение
        Signal decision = analyze(latestCandles);

        // Логика для выполнения ордера в зависимости от сигнала
        if (decision == Signal.BUY) {
            log.info("💡 Сигнал на покупку. Размещение ордера...");
            // Отправляем ордер на покупку
            // orderService.placeBuyOrder(chatId, "BTCUSDT", qty, true);
            // Уведомляем пользователя
            // notificationService.sendTradeNotification(chatId, Signal.BUY);
        } else if (decision == Signal.SELL) {
            log.info("💡 Сигнал на продажу. Размещение ордера...");
            // Отправляем ордер на продажу
            // orderService.placeSellOrder(chatId, "BTCUSDT", qty, true);
            // Уведомляем пользователя
            // notificationService.sendTradeNotification(chatId, Signal.SELL);
        } else {
            log.info("⚪️ Сигнал на удержание. Сделка не размещена.");
        }
    }
}
