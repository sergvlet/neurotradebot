package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.EmaConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmaStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private EmaConfig config = new EmaConfig(); // Конфигурация по умолчанию

    @Override
    public String getName() {
        return "EMA (" + config.getShortPeriod() + "/" + config.getLongPeriod() + ")";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getLongPeriod()) {
            log.warn("📉 Недостаточно данных для анализа EMA");
            return Signal.HOLD;  // Если данных недостаточно для анализа, возвращаем HOLD
        }

        // Строим серию баров для анализа
        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        // Рассчитываем короткую и длинную EMA
        EMAIndicator shortEma = new EMAIndicator(closePrice, config.getShortPeriod());
        EMAIndicator longEma = new EMAIndicator(closePrice, config.getLongPeriod());

        int lastIndex = series.getEndIndex();
        double shortValue = shortEma.getValue(lastIndex).doubleValue();
        double longValue = longEma.getValue(lastIndex).doubleValue();

        log.info("📊 EMA анализ: short={} long={}", shortValue, longValue);

        // Логика принятия решения: покупка или продажа на основе пересечения EMA
        if (shortValue > longValue) return Signal.BUY;  // Короткая EMA выше длинной — сигнал на покупку
        else if (shortValue < longValue) return Signal.SELL;  // Короткая EMA ниже длинной — сигнал на продажу
        else return Signal.HOLD;  // Если EMAs пересекаются или равны, удерживаем позицию
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof EmaConfig casted) {
            this.config = casted;
        } else {
            log.warn("❌ Некорректная конфигурация для EmaStrategy: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии EMA...");

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
