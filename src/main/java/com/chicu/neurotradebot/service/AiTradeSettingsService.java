// src/main/java/com/chicu/neurotradebot/service/AiTradeSettingsService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;

public interface AiTradeSettingsService {
    /**
     * Получить или создать настройки пользователя.
     */
    AiTradeSettings getOrCreate(User user);

    /**
     * Сохранить текущие настройки.
     */
    void save(AiTradeSettings settings);
}
