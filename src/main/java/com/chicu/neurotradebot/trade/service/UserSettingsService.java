package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.trade.model.TradeMode;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.trade.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository repository;

    // ⏳ Хранилище ожиданий текстового ввода по chatId
    private final Map<Long, String> waitingInputs = new HashMap<>();

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
     * Переключить стратегию
     */
    public void toggleStrategy(Long chatId, AvailableStrategy strategy) {
        UserSettings settings = getOrCreate(chatId);
        Set<AvailableStrategy> strategies = settings.getStrategies();

        if (strategies.contains(strategy)) {
            strategies.remove(strategy);
        } else {
            strategies.add(strategy);
        }

        settings.setStrategies(strategies);
        repository.save(settings);
    }

    public Set<AvailableStrategy> getSelectedStrategies(Long chatId) {
        return getOrCreate(chatId).getStrategies();
    }

    public void setStrategies(Long chatId, Set<AvailableStrategy> strategies) {
        UserSettings settings = getOrCreate(chatId);
        settings.setStrategies(strategies);
        repository.save(settings);
    }

    public void setTradeMode(Long chatId, TradeMode mode) {
        UserSettings settings = getOrCreate(chatId);
        settings.setTradeMode(mode);
        repository.save(settings);
    }

    public TradeMode getTradeMode(Long chatId) {
        return getOrCreate(chatId).getTradeMode();
    }

    /**
     * Ожидание текстового ввода
     */
    public void setWaitingForInput(Long chatId, String inputType) {
        waitingInputs.put(chatId, inputType);
    }

    public boolean isWaitingFor(Long chatId, String inputType) {
        return inputType.equals(waitingInputs.get(chatId));
    }

    public void clearWaiting(Long chatId) {
        waitingInputs.remove(chatId);
    }

    public String getWaitingInputType(Long chatId) {
        return waitingInputs.get(chatId);
    }

    /**
     * Лимит сделки
     */
    public void setTradeLimit(Long chatId, Double limit) {
        UserSettings settings = getOrCreate(chatId);
        settings.setTradeLimit(String.valueOf(limit));
        repository.save(settings);
    }

    public Double getTradeLimit(Long chatId) {
        return Double.valueOf(getOrCreate(chatId).getTradeLimit());
    }
}
