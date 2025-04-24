package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.CciConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CCIIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CciStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private CciConfig config = new CciConfig(); // Конфигурация для CCI

    @Override
    public String getName() {
        return "CCI";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod()) {
            log.warn("📉 Недостаточно данных для CCI");
            return Signal.HOLD; // Если данных недостаточно для анализа, возвращаем HOLD
        }

        // Строим серию баров для анализа
        BarSeries series = candleService.buildBarSeries(candles);
        CCIIndicator cci = new CCIIndicator(series, config.getPeriod());

        // Индекс последней свечи
        int lastIndex = series.getEndIndex();
        double cciValue = cci.getValue(lastIndex).doubleValue();

        log.info("📊 CCI: {}", cciValue);

        // Логика принятия решения: покупка, продажа или удержание
        if (cciValue < config.getOversoldThreshold()) return Signal.BUY;  // Если CCI ниже порога перепроданности
        else if (cciValue > config.getOverboughtThreshold()) return Signal.SELL;  // Если CCI выше порога перекупленности
        else return Signal.HOLD;  // Если CCI в пределах нормального диапазона
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof CciConfig cciConfig) {
            this.config = cciConfig;
        } else {
            log.warn("❌ Неверная конфигурация для CciStrategy: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии CCI...");

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
