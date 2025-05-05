// src/main/java/com/chicu/neurotradebot/service/ApiCredentialsService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;

import java.util.List;

public interface ApiCredentialsService {
    boolean hasCredentials(User user, String exchange, boolean testMode);

    void saveApiKey(User user, String exchange, boolean testMode, String apiKey);
    void saveApiSecret(User user, String exchange, boolean testMode, String apiSecret);

    List<ApiCredentials> listCredentials(User user, String exchange, boolean testMode);

    /**
     * Возвращает выбранные (активные) API-учётные данные для пользователя,
     * биржи и режима (test/prod).
     */
    ApiCredentials getSelectedCredential(User user, String exchange, boolean testMode);

    void selectCredential(User user, String exchange, boolean testMode, String label);
    boolean testConnection(User user, String exchange, boolean testMode);
}
