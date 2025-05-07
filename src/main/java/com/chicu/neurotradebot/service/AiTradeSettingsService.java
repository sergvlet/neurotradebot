// src/main/java/com/chicu/neurotradebot/service/AiTradeSettingsService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;

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

    
}
