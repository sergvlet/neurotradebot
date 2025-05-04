// src/main/java/com/chicu/neurotradebot/service/impl/AiTradeSettingsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.entity.UserInputState;
import com.chicu.neurotradebot.exchange.binance.BinanceApiClient;
import com.chicu.neurotradebot.exchange.binance.BinanceClientFactory;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.repository.ApiCredentialsRepository;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiTradeSettingsServiceImpl implements AiTradeSettingsService {

    private final AiTradeSettingsRepository repo;
    private final ApiCredentialsRepository credentialsRepo;
    private final BinanceClientFactory clientFactory;

    @Override
    @Transactional
    public AiTradeSettings getOrCreate(User user) {
        return repo.findByUser(user)
                .map(settings -> {
                    if (settings.getInputState() == null) {
                        settings.setInputState(UserInputState.NONE);
                        repo.save(settings);
                    }
                    return settings;
                })
                .orElseGet(() -> {
                    AiTradeSettings s = AiTradeSettings.builder()
                            .user(user)
                            .exchange(null)
                            .aiEnabled(false)
                            .testMode(false)
                            .scanIntervalSeconds(60)
                            .selectedPair(null)
                            .inputState(UserInputState.NONE)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return repo.save(s);
                });
    }

    @Override
    @Transactional
    public void save(AiTradeSettings settings) {
        repo.save(settings);
    }

    @Override
    @Transactional
    public void setTestMode(User user, boolean testMode) {
        AiTradeSettings s = getOrCreate(user);
        s.setTestMode(testMode);
        repo.save(s);
    }

    @Override
    public boolean isAiEnabled(User user) {
        return getOrCreate(user).isAiEnabled();
    }

    @Override
    public boolean testConnection(User user, String exchange, boolean testMode) {
        ApiCredentials credentials = credentialsRepo.findByUserAndExchangeAndTestMode(user, exchange, testMode)
                .orElseThrow(() -> new IllegalStateException("Ключи не найдены"));

        BinanceApiClient client = clientFactory.create(
                credentials.getApiKey(),
                credentials.getApiSecret(),
                testMode
        );
        try {
            client.getAccountInfo();
            return true;
        } catch (Exception e) {
            log.warn("❌ Не удалось подключиться к Binance API: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void setInputState(User user, UserInputState state) {
        AiTradeSettings settings = getOrCreate(user);
        settings.setInputState(state);
        repo.save(settings);
    }
}
