package com.chicu.neurotradebot.user.service;

import com.chicu.neurotradebot.user.enums.ApiKeySetupStage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiKeySetupService {

    private final ExchangeSettingsService exchangeSettingsService;

    private final Map<Long, ApiKeySetupStage> userStages = new HashMap<>();
    private final Map<Long, String> tempApiKeys = new HashMap<>();
    private final Map<Long, String> selectedExchanges = new HashMap<>();
    private final Map<Long, Boolean> useTestnetFlags = new HashMap<>();

    public void startSetup(Long chatId) {
        userStages.put(chatId, ApiKeySetupStage.ENTER_API_KEY);

        boolean useTestnet = exchangeSettingsService.isTestnetEnabled(chatId, "BINANCE");
        useTestnetFlags.put(chatId, useTestnet);
    }

    public ApiKeySetupStage getUserStage(Long chatId) {
        return userStages.get(chatId);
    }

    public void setUserStage(Long chatId, ApiKeySetupStage stage) {
        userStages.put(chatId, stage);
    }

    public void saveTempApiKey(Long chatId, String apiKey) {
        tempApiKeys.put(chatId, apiKey);
    }

    public String getTempApiKey(Long chatId) {
        return tempApiKeys.get(chatId);
    }

    public void setSelectedExchange(Long chatId, String exchange) {
        selectedExchanges.put(chatId, exchange);
    }

    public String getSelectedExchange(Long chatId) {
        return selectedExchanges.get(chatId);
    }


    public boolean isUseTestnet(Long chatId) {
        return useTestnetFlags.getOrDefault(chatId, true); // по умолчанию true (тестнет)
    }

    public boolean isInSetup(Long chatId) {
        return userStages.containsKey(chatId);
    }

    public void completeSetup(Long chatId) {
        userStages.remove(chatId);
        tempApiKeys.remove(chatId);
        selectedExchanges.remove(chatId);
        useTestnetFlags.remove(chatId);
    }
}
