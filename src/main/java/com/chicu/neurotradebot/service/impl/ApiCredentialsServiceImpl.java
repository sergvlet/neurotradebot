// src/main/java/com/chicu/neurotradebot/service/impl/ApiCredentialsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.ApiCredentialsRepository;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiCredentialsServiceImpl implements ApiCredentialsService {

    private final ApiCredentialsRepository repo;

    @Override
    public boolean hasCredentials(User user, String exchange, boolean testMode) {
        return repo.findByUserAndExchangeAndTestMode(user, exchange, testMode).isPresent();
    }

    @Override
    @Transactional
    public void saveApiKey(User user, String exchange, boolean testMode, String apiKey) {
        ApiCredentials c = repo.findByUserAndExchangeAndTestMode(user, exchange, testMode)
            .orElseGet(() -> ApiCredentials.builder()
                .user(user)
                .exchange(exchange)
                .testMode(testMode)
                .build());
        c.setApiKeyEncrypted(encrypt(apiKey));
        repo.save(c);
    }

    @Override
    @Transactional
    public void saveApiSecret(User user, String exchange, boolean testMode, String apiSecret) {
        ApiCredentials c = repo.findByUserAndExchangeAndTestMode(user, exchange, testMode)
            .orElseThrow(); // ключ должен быть уже сохранён
        c.setApiSecretEncrypted(encrypt(apiSecret));
        repo.save(c);
    }

    @Override
    public ApiCredentials get(User user, String exchange, boolean testMode) {
        return repo.findByUserAndExchangeAndTestMode(user, exchange, testMode)
            .orElseThrow();
    }

    private String encrypt(String plain) {
        // TODO: ваша логика шифрования
        return plain; 
    }
}
