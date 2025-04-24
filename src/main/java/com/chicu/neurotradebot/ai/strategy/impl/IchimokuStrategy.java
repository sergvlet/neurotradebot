package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.IchimokuConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuKijunSenIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuTenkanSenIndicator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IchimokuStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private IchimokuConfig config = new IchimokuConfig(); // Конфигурация по умолчанию

    @Override
    public String getName() {
        return "Ichimoku";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getBaseLinePeriod() + 1) {
            log.warn("📉 Недостаточно данных для Ichimoku (минимум {} баров)", config.getBaseLinePeriod() + 1);
            return Signal.HOLD;  // Если данных недостаточно для анализа, возвращаем HOLD
        }

        BarSeries series = candleService.buildBarSeries(candles);

        IchimokuTenkanSenIndicator tenkanSen = new IchimokuTenkanSenIndicator(series, config.getConversionLinePeriod());
        IchimokuKijunSenIndicator kijunSen = new IchimokuKijunSenIndicator(series, config.getBaseLinePeriod());
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        int lastIndex = series.getEndIndex();
        double tenkan = tenkanSen.getValue(lastIndex).doubleValue();
        double kijun = kijunSen.getValue(lastIndex).doubleValue();
        double price = close.getValue(lastIndex).doubleValue();

        log.info("📊 Ichimoku: price={}, tenkan={}, kijun={}", price, tenkan, kijun);

        // Логика принятия решения на основе Ichimoku
        if (tenkan > kijun && price > tenkan) {
            return Signal.BUY;
        } else if (tenkan < kijun && price < tenkan) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof IchimokuConfig ichimokuConfig) {
            this.config = ichimokuConfig;
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии Ichimoku...");

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
