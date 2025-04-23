package com.chicu.neurotradebot.ai;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import com.chicu.neurotradebot.trade.service.StrategyConfigService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyEngine {

    private final MarketCandleService candleService;
    private final UserSettingsService userSettingsService;
    private final StrategyConfigService configService;

    // фабрика стратегий
    private final com.chicu.neurotradebot.ai.strategy.StrategyFactory strategyFactory;

    /**
     * Анализ всех выбранных стратегий пользователя
     */
    public Map<AvailableStrategy, Signal> analyzeAll(Long chatId, String symbol, String interval) {
        Set<AvailableStrategy> selected = userSettingsService.getOrCreate(chatId).getStrategies();
        List<MarketCandle> candles = candleService.getCandles(symbol, interval);

        Map<AvailableStrategy, Signal> result = new LinkedHashMap<>();

        for (AvailableStrategy strategyKey : selected) {
            AiStrategy strategy = strategyFactory.get(strategyKey);
            if (strategy == null) continue;

            // Установка конфигурации (универсально)
            Object config = configService.getConfig(chatId, strategyKey);
            strategy.setConfig(config);

            // Анализ
            Signal signal = strategy.analyze(candles);
            result.put(strategyKey, signal);
        }

        return result;
    }
}
