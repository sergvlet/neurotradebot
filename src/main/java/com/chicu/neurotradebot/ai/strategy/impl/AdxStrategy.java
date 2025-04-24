package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.AdxConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.adx.ADXIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdxStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private AdxConfig config = new AdxConfig(); // По умолчанию

    @Override
    public String getName() {
        return "ADX";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod() + 1) {
            log.warn("📉 Недостаточно данных для ADX (нужно минимум {} баров)", config.getPeriod() + 1);
            return Signal.HOLD;  // Если данных недостаточно, возвращаем HOLD
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ADXIndicator adx = new ADXIndicator(series, config.getPeriod());

        // Получаем текущее значение ADX
        double value = adx.getValue(series.getEndIndex()).doubleValue();

        log.info("📊 ADX: value={}, threshold={}", value, config.getTrendStrengthThreshold());

        // Логика принятия решения: если ADX выше порога, то сигнал на покупку
        return value > config.getTrendStrengthThreshold() ? Signal.BUY : Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof AdxConfig) {
            this.config = (AdxConfig) config;
        } else {
            log.warn("⚠️ Неверный тип конфигурации для ADX: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии ADX...");

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
        } else {
            log.info("⚪️ Сигнал на удержание. Сделка не размещена.");
        }
    }
}
