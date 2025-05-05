// src/main/java/com/chicu/neurotradebot/service/impl/ApiCredentialsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.ApiCredentialsRepository;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.exchange.binance.BinanceClientFactory;
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

    /**
     * Возвращает список всех сохранённых API-учётных данных для данного пользователя,
     * биржи и режима (real или testnet).
     *
     * @param user      пользователь, для которого запрашиваем данные
     * @param exchange  код биржи, например "binance"
     * @param testMode  true — testnet, false — real
     * @return список сущностей ApiCredentials, соответствующих фильтру
     */
    @Override
    public List<ApiCredentials> listCredentials(User user, String exchange, boolean testMode) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        String label = exchange + (testMode ? "_testnet" : "_main");
        return repository.findAllBySettings(settings).stream()
                .filter(c -> label.equals(c.getLabel()))
                .toList();
    }

    /**
     * Проверяет, сохранены ли вообще ключи для данного пользователя,
     * биржи и режима.
     *
     * @param user      пользователь
     * @param exchange  код биржи
     * @param testMode  режим testnet или real
     * @return true, если найдены хотя бы одни креды
     */
    @Override
    public boolean hasCredentials(User user, String exchange, boolean testMode) {
        return !listCredentials(user, exchange, testMode).isEmpty();
    }

    /**
     * Сохраняет API Key для пользователя. Если записи с таким лейблом ещё нет,
     * создаёт новую сущность ApiCredentials. Секрет помечается "PENDING", данные неактивны.
     *
     * @param user      пользователь
     * @param exchange  код биржи
     * @param testMode  режим testnet или real
     * @param apiKey    строка API Key
     */
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
                label, user.getTelegramUserId(), exchange, testMode ? "TESTNET" : "REAL");
    }

    /**
     * Сохраняет API Secret для ранее созданного ApiCredentials, делает запись активной.
     *
     * @param user       пользователь
     * @param exchange   код биржи
     * @param testMode   режим testnet или real
     * @param apiSecret  строка API Secret
     */
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
                label, user.getTelegramUserId(), exchange, testMode ? "TESTNET" : "REAL");
    }

    /**
     * Отмечает указанную запись ApiCredentials активной, остальные — неактивными.
     *
     * @param user      пользователь
     * @param exchange  код биржи
     * @param testMode  режим testnet или real
     * @param label     лейбл записи, которую нужно активировать
     */
    @Override
    @Transactional
    public void selectCredential(User user, String exchange, boolean testMode, String label) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        List<ApiCredentials> credsList = repository.findAllBySettings(settings);

        credsList.forEach(c -> c.setActive(label.equals(c.getLabel())));
        repository.saveAll(credsList);

        log.info("✅ Активированы креды '{}' для user={} exchange={} mode={}",
                label, user.getTelegramUserId(), exchange, testMode ? "TESTNET" : "REAL");
    }

    /**
     * Возвращает активную запись ApiCredentials для данного пользователя,
     * биржи и режима. Если нет активных, бросает исключение.
     *
     * @param user      пользователь
     * @param exchange  код биржи
     * @param testMode  режим testnet или real
     * @return активная сущность ApiCredentials
     */
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
     *
     * @param user      пользователь
     * @param exchange  код биржи
     * @param testMode  режим testnet или real
     * @return true, если соединение успешно, иначе бросает RuntimeException
     */
    @Override
    public boolean testConnection(User user, String exchange, boolean testMode) {
        ApiCredentials creds = getSelectedCredential(user, exchange, testMode);
        try {
            var client = clientFactory.create(
                    creds.getApiKey(), creds.getApiSecret(), testMode
            );
            client.getAccountInfo();
            return true;
        } catch (Exception e) {
            log.error("❌ Ошибка тестирования соединения exchange={} mode={} — {}",
                    exchange, testMode ? "TESTNET" : "REAL", e.getMessage());
            throw new RuntimeException("Не удалось подключиться к бирже", e);
        }
    }
}
