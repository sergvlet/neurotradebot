package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.DonchianChannelConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.*;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DonchianChannelStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private DonchianChannelConfig config = new DonchianChannelConfig(); // по умолчанию

    @Override
    public String getName() {
        return "Donchian Channel";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod()) {
            log.warn("📉 Недостаточно данных для Donchian Channel");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        HighPriceIndicator highPrice = new HighPriceIndicator(series);
        LowPriceIndicator lowPrice = new LowPriceIndicator(series);
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        HighestValueIndicator upper = new HighestValueIndicator(highPrice, config.getPeriod());
        LowestValueIndicator lower = new LowestValueIndicator(lowPrice, config.getPeriod());

        int lastIndex = series.getEndIndex();
        double closeValue = close.getValue(lastIndex).doubleValue();
        double upperValue = upper.getValue(lastIndex).doubleValue();
        double lowerValue = lower.getValue(lastIndex).doubleValue();

        log.info("📊 Donchian Channel: close={}, upper={}, lower={}", closeValue, upperValue, lowerValue);

        if (closeValue > upperValue) return Signal.BUY;
        else if (closeValue < lowerValue) return Signal.SELL;
        else return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof DonchianChannelConfig casted) {
            this.config = casted;
        } else {
            log.warn("❌ Некорректная конфигурация для DonchianChannelStrategy: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии Donchian Channel...");

        // Получаем последние данные для анализа (например, 100 свечей)
        List<MarketCandle> latestCandles = candleService.getLatestCandles("BTCUSDT", "1h", 100);

        // Выполняем анализ и получаем решение
        Signal decision = analyze(latestCandles);

        // Реализуем логику для исполнения ордера в зависимости от сигнала
        if (decision == Signal.BUY) {
            log.info("💡 Сигнал на покупку. Размещение ордера...");
            // Отправляем ордер на покупку
            // Например, в OrderService можно разместить ордер
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
