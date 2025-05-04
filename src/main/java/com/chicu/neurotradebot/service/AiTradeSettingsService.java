package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.entity.UserInputState;

public interface AiTradeSettingsService {

    AiTradeSettings getOrCreate(User user);

    void save(AiTradeSettings settings);

    void setTestMode(User user, boolean testMode);

    boolean isAiEnabled(User user);

    boolean testConnection(User user, String exchange, boolean testMode);

    void setInputState(User user, UserInputState state);
}
