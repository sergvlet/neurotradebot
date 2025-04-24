package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.model.StrategyConfigEntity;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.trade.repository.StrategyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStrategyConfigService {

    private final StrategyConfigRepository strategyConfigRepository;
    private final UserSettingsService userSettingsService;

    // Сохранение конфигурации стратегии
    public void saveUserStrategyConfig(Long chatId, AvailableStrategy strategy, StrategyConfig config) {
        UserSettings userSettings = userSettingsService.getOrCreate(chatId);

        // Проверяем, существует ли уже конфигурация для этой стратегии
        StrategyConfigEntity existingConfig = strategyConfigRepository
                .findByUserSettingsAndStrategy(userSettings, strategy);

        if (existingConfig != null) {
            existingConfig.setConfigData(config.toString()); // Обновляем конфигурацию
        } else {
            // Создаем новую конфигурацию и сохраняем её
            StrategyConfigEntity newConfig = new StrategyConfigEntity(userSettings, strategy, config);
            strategyConfigRepository.save(newConfig);
        }
    }

    // Получение конфигурации стратегии
    public StrategyConfig getUserStrategyConfig(Long chatId, AvailableStrategy strategy) {
        UserSettings userSettings = userSettingsService.getOrCreate(chatId);
        StrategyConfigEntity configEntity = strategyConfigRepository
                .findByUserSettingsAndStrategy(userSettings, strategy);

        return configEntity != null ? configEntity.getConfig() : null;
    }

    // Сброс конфигурации стратегии
    public void resetUserStrategyConfig(Long chatId, AvailableStrategy strategy) {
        UserSettings userSettings = userSettingsService.getOrCreate(chatId);
        StrategyConfigEntity configEntity = strategyConfigRepository
                .findByUserSettingsAndStrategy(userSettings, strategy);

        if (configEntity != null) {
            strategyConfigRepository.delete(configEntity); // Удаляем конфигурацию
        }
    }
}
