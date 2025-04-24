package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.HybridAiConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.OrderService;
import com.chicu.neurotradebot.trade.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HybridAiStrategy implements AiStrategy {

    private final List<AiStrategy> strategies;
    private final OrderService orderService; // Для размещения ордеров
    private final TelegramNotificationService notificationService; // Для уведомлений
    private HybridAiConfig config = new HybridAiConfig();

    @Override
    public String getName() {
        return "Hybrid AI";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (strategies.isEmpty()) {
            log.warn("⚠️ Нет доступных стратегий для Hybrid AI");
            return Signal.HOLD;  // Если нет доступных стратегий, возвращаем HOLD
        }

        Map<Signal, Integer> signalCounts = new EnumMap<>(Signal.class);
        for (Signal s : Signal.values()) signalCounts.put(s, 0);

        // Фильтруем стратегии, если в конфигурации указаны конкретные стратегии
        List<AiStrategy> selectedStrategies = strategies.stream()
                .filter(s -> config.getStrategyNames() == null || config.getStrategyNames().contains(s.getName()))
                .toList();

        // Анализируем каждую стратегию
        for (AiStrategy strategy : selectedStrategies) {
            try {
                Signal signal = strategy.analyze(candles);
                signalCounts.put(signal, signalCounts.get(signal) + 1);
            } catch (Exception e) {
                log.warn("❌ Ошибка в стратегии {}: {}", strategy.getName(), e.getMessage());
            }
        }

        // Получаем результаты голосования
        int total = selectedStrategies.size();
        int buyVotes = signalCounts.get(Signal.BUY);
        int sellVotes = signalCounts.get(Signal.SELL);

        log.info("📊 Hybrid голосование: BUY={} SELL={} HOLD={}", buyVotes, sellVotes, signalCounts.get(Signal.HOLD));

        // Логика принятия решения
        if ((double) buyVotes / total > config.getThreshold()) return Signal.BUY;
        if ((double) sellVotes / total > config.getThreshold()) return Signal.SELL;

        return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof HybridAiConfig hybridConfig) {
            this.config = hybridConfig;
        } else {
            log.warn("❌ Неверная конфигурация для Hybrid AI: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии Hybrid AI...");

        // Получаем последние данные для анализа (например, 100 свечей)
        List<MarketCandle> latestCandles = new ArrayList<>();  // Получите последние свечи с вашего сервиса
        // Пример: latestCandles = candleService.getLatestCandles("BTCUSDT", "1h", 100);

        // Выполняем анализ и получаем решение
        Signal decision = analyze(latestCandles);

        // Логика для выполнения ордера
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
