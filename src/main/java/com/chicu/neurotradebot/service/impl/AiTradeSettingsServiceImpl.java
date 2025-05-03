// src/main/java/com/chicu/neurotradebot/service/impl/AiTradeSettingsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsServiceImpl implements AiTradeSettingsService {

    private final AiTradeSettingsRepository repo;


    @Override
    @Transactional
    public AiTradeSettings getOrCreate(User user) {
        return repo.findByUser(user)
                .orElseGet(() -> {
                    AiTradeSettings s = AiTradeSettings.builder()
                            .user(user)
                            .exchange(null)
                            .aiEnabled(false)
                            .testMode(false)
                            .scanIntervalSeconds(60)
                            .selectedPair(null)
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
}
