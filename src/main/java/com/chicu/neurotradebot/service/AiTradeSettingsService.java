// src/main/java/com/chicu/neurotradebot/service/AiTradeSettingsService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.enums.ConfigWaiting;
import com.chicu.neurotradebot.enums.StrategyType;

import java.util.List;

public interface AiTradeSettingsService {
    /**
     * Получить или создать настройки пользователя.
     */
    AiTradeSettings getOrCreate(User user);

    /**
     * Сохранить текущие настройки.
     */
    void save(AiTradeSettings settings);

    /** Получить настройки для «текущего» пользователя (из BotContext). */
    AiTradeSettings getForCurrentUser();
    /** Найти настройки по Telegram chatId (user.id). */
    AiTradeSettings getByChatId(Long chatId);

    /** Вернуть все настройки, у которых включена автоматическая торговля (enabled==true). */
    List<AiTradeSettings> findAllActive();
    /**
     * Помечает, что для данного chatId бот ожидает ввода конкретного параметра.
     */
    void markWaiting(Long chatId, ConfigWaiting what);
    ConfigWaiting getWaiting(Long chatId);
    void clearWaiting(Long chatId);
    void toggleStrategy(Long chatId, StrategyType type);
    void toggleMlTpSl(Long chatId);
    void updateMlTotalCapital(Long chatId, double deltaUsd);
    void updateMlEntryRsiThreshold(Long chatId, double deltaRsi);
    void updateMlLookbackPeriod(Long chatId, int deltaHours);
    void resetMlConfig(Long chatId);
    // для ввода URL

}
