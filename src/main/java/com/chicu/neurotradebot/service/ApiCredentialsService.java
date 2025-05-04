// src/main/java/com/chicu/neurotradebot/service/ApiCredentialsService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;

public interface ApiCredentialsService {
    boolean hasCredentials(User user, String exchange, boolean testMode);
    void saveApiKey(User user, String exchange, boolean testMode, String apiKey);
    void saveApiSecret(User user, String exchange, boolean testMode, String apiSecret);
    ApiCredentials get(User user, String exchange, boolean testMode);
    boolean testConnection(User user, String exchange, boolean testMode);


}
