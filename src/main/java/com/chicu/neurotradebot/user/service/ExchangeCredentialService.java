package com.chicu.neurotradebot.user.service;

import com.chicu.neurotradebot.user.entity.ExchangeCredential;
import com.chicu.neurotradebot.user.entity.ExchangeSettings;
import com.chicu.neurotradebot.user.repository.ExchangeCredentialRepository;
import com.chicu.neurotradebot.user.repository.ExchangeSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeCredentialService {

    private final ExchangeCredentialRepository credentialRepository;
    private final ExchangeSettingsRepository settingsRepository;

    public String getApiKey(Long userId, boolean useTestnet) {
        ExchangeCredential credential = credentialRepository.findByUserIdAndExchange(userId, "BINANCE")
                .orElseThrow(() -> new RuntimeException("API-ключи Binance не найдены для пользователя"));
        return useTestnet ? credential.getTestApiKey() : credential.getRealApiKey();
    }

    public String getSecretKey(Long userId, boolean useTestnet) {
        ExchangeCredential credential = credentialRepository.findByUserIdAndExchange(userId, "BINANCE")
                .orElseThrow(() -> new RuntimeException("API-ключи Binance не найдены для пользователя"));
        return useTestnet ? credential.getTestSecretKey() : credential.getRealSecretKey();
    }

    public String getBaseUrl(Long userId, boolean useTestnet) {
        return useTestnet
                ? "https://testnet.binance.vision"
                : "https://api.binance.com";
    }

    public boolean isTestnetEnabled(Long userId) {
        ExchangeSettings settings = settingsRepository.findByUserIdAndExchange(userId, "BINANCE")
                .orElseThrow(() -> new RuntimeException("Настройки Binance не найдены для пользователя"));
        return Boolean.TRUE.equals(settings.getUseTestnet());
    }

}
