package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.StrategyFactory;
import com.chicu.neurotradebot.trade.enums.TradeType;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyRunnerServiceImpl {

    private final StrategyFactory strategyFactory;
    private final UserSettingsService userSettingsService;

    public void run(Long chatId) {
        UserSettings settings = userSettingsService.getOrCreate(chatId);

        AvailableStrategy strategy;

        // Проверяем режим торговли: AI или Manual
        if (settings.getTradeType() == TradeType.AI) {
            // Выбираем стратегию для AI (если стратегия не выбрана, используем дефолтную)
            strategy = settings.getStrategies().isEmpty() ? AvailableStrategy.MACD : settings.getStrategies().iterator().next();
        } else if (settings.getTradeType() == TradeType.MANUAL) {
            // Выбираем стратегию для ручной торговли
            strategy = settings.getSelectedManualStrategy();
        } else {
            throw new IllegalArgumentException("Неизвестный тип торговли.");
        }

        // Получаем стратегию из фабрики
        AiStrategy aiStrategy = strategyFactory.get(strategy);

        // Запуск стратегии
        aiStrategy.execute();
    }
}
