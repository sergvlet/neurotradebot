package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.LstmConfig;
import com.chicu.neurotradebot.ai.strategy.ml.PricePredictor;
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
public class LstmForecastStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final PricePredictor predictor; // Для предсказания цен с использованием модели LSTM
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для отправки уведомлений
    private LstmConfig config = new LstmConfig(); // Конфигурация по умолчанию

    @Override
    public String getName() {
        return "LSTM Forecast";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getWindowSize()) {
            log.warn("📉 Недостаточно данных для LSTM стратегии");
            return Signal.HOLD;  // Если данных недостаточно, возвращаем HOLD
        }

        // Составляем список исторических данных для прогноза
        List<Double> history = new ArrayList<>();
        for (int i = candles.size() - config.getWindowSize(); i < candles.size(); i++) {
            history.add(candles.get(i).getClose()); // Закрывающие цены
        }

        // Прогнозируем следующую цену
        double forecast = predictor.predictNextPrice(history);
        double lastPrice = candles.get(candles.size() - 1).getClose(); // Текущая цена

        log.info("🤖 LSTM: прогноз={}, текущая цена={}", forecast, lastPrice);

        // Логика принятия решения: покупка, продажа или удержание
        if (forecast > lastPrice * 1.002) return Signal.BUY;  // Прогнозируемая цена выше на 0.2% — сигнал на покупку
        if (forecast < lastPrice * 0.998) return Signal.SELL; // Прогнозируемая цена ниже на 0.2% — сигнал на продажу
        return Signal.HOLD;  // Если цена не изменилась существенно, удерживаем позицию
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof LstmConfig lstmConfig) {
            this.config = lstmConfig;
        } else {
            log.warn("❌ Неверная конфигурация передана в LSTM стратегию: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии LSTM Forecast...");

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
