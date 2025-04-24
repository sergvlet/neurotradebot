package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.RsiConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RsiStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private RsiConfig config = new RsiConfig(); // Конфигурация по умолчанию

    @Override
    public String getName() {
        return "RSI";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod() + 1) {
            log.warn("📉 Недостаточно данных для RSI анализа");
            return Signal.HOLD; // Если данных недостаточно, возвращаем HOLD
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(closePrice, config.getPeriod());

        int lastIndex = series.getEndIndex();
        double rsiValue = rsi.getValue(lastIndex).doubleValue();

        log.info("📊 RSI = {}", rsiValue);

        if (rsiValue < config.getOversold()) {
            return Signal.BUY; // Если RSI меньше порога перепроданности, покупаем
        } else if (rsiValue > config.getOverbought()) {
            return Signal.SELL; // Если RSI больше порога перекупленности, продаем
        } else {
            return Signal.HOLD; // Если RSI в пределах нормального диапазона, удерживаем
        }
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof RsiConfig rsiConfig) {
            this.config = rsiConfig;
        } else {
            log.warn("❌ Неверная конфигурация для RSI стратегии: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии RSI...");

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
