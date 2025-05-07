package com.chicu.neurotradebot.service.impl;

import com.binance.connector.client.exceptions.BinanceClientException;
import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.ApiCredentialsRepository;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления API-учётными данными пользователя.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCredentialsServiceImpl implements ApiCredentialsService {

    private final ApiCredentialsRepository repository;
    private final AiTradeSettingsService settingsService;
    private final BinanceClientFactory clientFactory;

    @Override
    public List<ApiCredentials> listCredentials(User user, String exchange, boolean testMode) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        String label = exchange + (testMode ? "_testnet" : "_main");
        return repository.findAllBySettings(settings).stream()
                .filter(c -> label.equals(c.getLabel()))
                .toList();
    }

    @Override
    public boolean hasCredentials(User user, String exchange, boolean testMode) {
        return !listCredentials(user, exchange, testMode).isEmpty();
    }

    @Override
    @Transactional
    public void saveApiKey(User user, String exchange, boolean testMode, String apiKey) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        String label = exchange + (testMode ? "_testnet" : "_main");
        ApiCredentials creds = repository.findBySettingsAndLabel(settings, label)
                .orElseGet(() -> {
                    ApiCredentials c = new ApiCredentials();
                    c.setUser(user);
                    c.setSettings(settings);
                    c.setLabel(label);
                    return c;
                });
        creds.setApiKey(apiKey);
        creds.setApiSecret("PENDING");
        creds.setActive(false);
        repository.save(creds);
        log.info("🔑 Сохранён API Key (label={}) для user={} exchange={} mode={}",
                label, user.getTelegramUserId(), testMode ? "TESTNET" : "REAL");
    }

    @Override
    @Transactional
    public void saveApiSecret(User user, String exchange, boolean testMode, String apiSecret) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        String label = exchange + (testMode ? "_testnet" : "_main");
        ApiCredentials creds = repository.findBySettingsAndLabel(settings, label)
                .orElseThrow(() -> new IllegalStateException("Сначала сохраните API Key"));
        creds.setApiSecret(apiSecret);
        creds.setActive(true);
        repository.save(creds);
        log.info("🔒 Сохранён API Secret (label={}) для user={} exchange={} mode={}",
                label, user.getTelegramUserId(), testMode ? "TESTNET" : "REAL");
    }

    @Override
    @Transactional
    public void selectCredential(User user, String exchange, boolean testMode, String label) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        List<ApiCredentials> credsList = repository.findAllBySettings(settings);
        credsList.forEach(c -> c.setActive(label.equals(c.getLabel())));
        repository.saveAll(credsList);
        log.info("✅ Активированы креды '{}' для user={} exchange={} mode={}",
                label, user.getTelegramUserId(), testMode ? "TESTNET" : "REAL");
    }

    @Override
    public ApiCredentials getSelectedCredential(User user, String exchange, boolean testMode) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        String label = exchange + (testMode ? "_testnet" : "_main");
        return repository.findBySettingsAndLabel(settings, label)
                .filter(ApiCredentials::isActive)
                .orElseThrow(() -> new IllegalStateException(
                        "Нет активных API-ключей для user=" + user.getTelegramUserId()));
    }

    /**
     * Проверяет соединение с биржей через Binance API: делает запрос accountInfo.
     * - При HTTP 2xx возвращает true.
     * - При HTTP 4xx (неверные ключи/подпись) возвращает false.
     * - При HTTP 5xx или других ошибках бросает RuntimeException.
     */
    @Override
    public boolean testConnection(User user, String exchange, boolean testMode) {
        ApiCredentials creds = getSelectedCredential(user, exchange, testMode);

        // Сразу логируем, какие креды использует метод:
        log.info("🔑 testConnection: userId={} exchange='{}' mode={} → using label='{}', apiKey='{}', apiSecret='{}'",
                user.getTelegramUserId(),
                exchange,
                testMode ? "TESTNET" : "REAL",
                creds.getLabel(),
                creds.getApiKey(),
                creds.getApiSecret().replaceAll(".", "*")  // весь secret звёздочками
        );

        var client = clientFactory.create(
                creds.getApiKey(),
                creds.getApiSecret(),
                testMode
        );

        try {
            client.getAccountInfo();
            log.info("✅ getAccountInfo OK for mode={}", testMode ? "TESTNET" : "REAL");
            return true;
        } catch (BinanceClientException e) {
            String body = e.getMessage();
            if (body != null && body.contains("\"code\":-1021")) {
                log.warn("⚠️ Timestamp ahead — ключи считаем валидными");
                return true;
            }
            log.warn("❌ Клиентская ошибка (4xx) — неверные ключи: {}", body);
            return false;
        } catch (Exception ex) {
            log.warn("❌ Сетевая/серверная ошибка при проверке ключей: {}", ex.getMessage());
            return false;
        }
    }


}