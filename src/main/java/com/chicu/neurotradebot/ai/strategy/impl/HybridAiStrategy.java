package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.HybridAiConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.Signal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Стратегия объединяет сигналы от других стратегий.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HybridAiStrategy implements AiStrategy {

    private final List<AiStrategy> strategies;
    private final HybridAiConfig config = new HybridAiConfig();

    @Override
    public String getName() {
        return "Hybrid AI";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (strategies.isEmpty()) {
            log.warn("⚠️ Нет доступных стратегий для Hybrid AI");
            return Signal.HOLD;
        }

        Map<Signal, Integer> signalCounts = new EnumMap<>(Signal.class);
        for (Signal s : Signal.values()) signalCounts.put(s, 0);

        List<AiStrategy> selectedStrategies = strategies.stream()
                .filter(s -> config.getStrategyNames() == null || config.getStrategyNames().contains(s.getName()))
                .toList();

        for (AiStrategy strategy : selectedStrategies) {
            try {
                Signal signal = strategy.analyze(candles);
                signalCounts.put(signal, signalCounts.get(signal) + 1);
            } catch (Exception e) {
                log.warn("❌ Ошибка в стратегии {}: {}", strategy.getName(), e.getMessage());
            }
        }

        int total = selectedStrategies.size();
        int buyVotes = signalCounts.get(Signal.BUY);
        int sellVotes = signalCounts.get(Signal.SELL);

        log.info("📊 Hybrid голосование: BUY={} SELL={} HOLD={}", buyVotes, sellVotes, signalCounts.get(Signal.HOLD));

        if ((double) buyVotes / total > config.getThreshold()) return Signal.BUY;
        if ((double) sellVotes / total > config.getThreshold()) return Signal.SELL;

        return Signal.HOLD;
    }
}
