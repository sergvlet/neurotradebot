// src/main/java/com/chicu/neurotradebot/service/AiTradeSettingsService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;

public interface AiTradeSettingsService {
    AiTradeSettings getOrCreate(User user);
    void save(AiTradeSettings settings);
    void setTestMode(User user, boolean testMode);
    boolean isAiEnabled(User user);
}
