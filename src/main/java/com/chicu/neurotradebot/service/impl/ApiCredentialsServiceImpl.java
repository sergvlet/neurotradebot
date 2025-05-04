// src/main/java/com/chicu/neurotradebot/service/impl/ApiCredentialsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.exchange.binance.BinanceApiClient;
import com.chicu.neurotradebot.exchange.binance.BinanceClientFactory;
import com.chicu.neurotradebot.repository.ApiCredentialsRepository;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCredentialsServiceImpl implements ApiCredentialsService {

    private final ApiCredentialsRepository repo;
    private final BinanceClientFactory clientFactory;

    @Override
    public boolean hasCredentials(User user, String exchange, boolean testMode) {
        return repo.findByUserAndExchangeAndTestMode(user, exchange, testMode).isPresent();
    }

    @Override
    @Transactional
    public void saveApiKey(User user, String exchange, boolean testMode, String apiKey) {
        ApiCredentials creds = repo.findByUserAndExchangeAndTestMode(user, exchange, testMode)
                .orElse(null);

        if (creds == null) {
            creds = new ApiCredentials();
            creds.setUser(user);
            creds.setExchange(exchange);
            creds.setTestMode(testMode);
            creds.setApiKey(encrypt(apiKey));
            creds.setApiSecret("PENDING"); // обязательное поле
            log.info("✅ Создан новый ApiCredentials с PENDING секретом для пользователя {} [{}] ({}, test={})",
                    user.getId(), exchange, user.getUsername(), testMode);
        } else {
            creds.setApiKey(encrypt(apiKey));
            log.info("🔁 Обновлён API Key для пользователя {} [{}] ({}, test={})",
                    user.getId(), exchange, user.getUsername(), testMode);
        }

        repo.save(creds);
    }

    @Override
    @Transactional
    public void saveApiSecret(User user, String exchange, boolean testMode, String apiSecret) {
        ApiCredentials creds = repo.findByUserAndExchangeAndTestMode(user, exchange, testMode)
                .orElseThrow(() -> new IllegalStateException(
                        "❌ Не найдена запись ApiCredentials перед сохранением API Secret. Проверь, вызывался ли saveApiKey."));

        if (creds.getApiKey() == null) {
            throw new IllegalStateException("❌ Сначала должен быть установлен API Key.");
        }

        creds.setApiSecret(encrypt(apiSecret));
        repo.save(creds);

        log.info("✅ API Secret успешно сохранён для пользователя {} [{}] (test={})",
                user.getId(), exchange, testMode);
    }

    @Override
    public ApiCredentials get(User user, String exchange, boolean testMode) {
        return repo.findByUserAndExchangeAndTestMode(user, exchange, testMode)
                .orElseThrow(() -> new IllegalStateException("❌ Данные API не найдены."));
    }

    @Override
    public boolean testConnection(User user, String exchange, boolean testMode) {
        ApiCredentials creds = get(user, exchange, testMode);
        try {
            BinanceApiClient client = clientFactory.create(creds.getApiKey(), creds.getApiSecret(), testMode);
            client.getAccountInfo();
            return true;
        } catch (Exception e) {
            log.warn("❌ Ошибка при проверке соединения с Binance: {}", e.getMessage());
            return false;
        }
    }

    private String encrypt(String plain) {
        // TODO: ваша логика шифрования
        return plain;
    }
}
