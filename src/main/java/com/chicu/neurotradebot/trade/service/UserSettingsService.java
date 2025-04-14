package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.trade.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository repository;

    /**
     * Получить или создать настройки пользователя
     */
    public UserSettings getOrCreate(Long chatId) {
        return repository.findByChatId(chatId)
                .orElseGet(() -> {
                    UserSettings settings = new UserSettings();
                    settings.setChatId(chatId);
                    settings.setStrategies(new HashSet<>());
                    return repository.save(settings);
                });
    }

    /**
     * Переключить стратегию: если выбрана — убрать, если не выбрана — добавить
     */
    public void toggleStrategy(Long chatId, AvailableStrategy strategy) {
        UserSettings settings = getOrCreate(chatId);
        Set<AvailableStrategy> strategies = settings.getStrategies();

        if (strategies.contains(strategy)) {
            strategies.remove(strategy);
        } else {
            strategies.add(AvailableStrategy.valueOf(String.valueOf(strategy)));
        }

        settings.setStrategies(strategies);
        repository.save(settings);
    }

    /**
     * Получить список выбранных стратегий пользователя
     */
    public Set<AvailableStrategy> getSelectedStrategies(Long chatId) {
        return getOrCreate(chatId).getStrategies();
    }

    /**
     * Установить конкретные стратегии
     */
    public void setStrategies(Long chatId, Set<AvailableStrategy> strategies) {
        UserSettings settings = getOrCreate(chatId);
        settings.setStrategies(strategies);
        repository.save(settings);
    }
}
