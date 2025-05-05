package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.exchange.binance.BinanceApiClient;
import com.chicu.neurotradebot.exchange.binance.BinanceClientFactory;
import com.chicu.neurotradebot.repository.ApiCredentialsRepository;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCredentialsServiceImpl implements ApiCredentialsService {

    private final ApiCredentialsRepository repo;
    private final AiTradeSettingsService settingsService;
    private final BinanceClientFactory clientFactory;

    @Override
    public List<ApiCredentials> listCredentials(User user, String exchange, boolean testMode) {
        // Получаем единственный AiTradeSettings для user, но не меняем в нём exchange/testMode
        AiTradeSettings settings = settingsService.getOrCreate(user);
        // Фильтруем по переданным параметрам:
        String expectedLabel = exchange + (testMode ? "_testnet" : "_main");
        return repo.findAllBySettings(settings).stream()
                .filter(c -> c.getLabel().equals(expectedLabel))
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

        ApiCredentials creds = repo.findBySettingsAndLabel(settings, label)
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
        repo.save(creds);

        log.info("🔑 API Key сохранён (label={}) для user={} биржа={} режим={}",
                label, user.getTelegramUserId(),
                exchange, testMode ? "TESTNET" : "REAL");
    }

    @Override
    @Transactional
    public void saveApiSecret(User user, String exchange, boolean testMode, String apiSecret) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        String label = exchange + (testMode ? "_testnet" : "_main");

        ApiCredentials creds = repo.findBySettingsAndLabel(settings, label)
                .orElseThrow(() -> new IllegalStateException("Сначала сохраните API Key"));

        creds.setApiSecret(apiSecret);
        creds.setActive(true);
        repo.save(creds);

        log.info("🔒 API Secret сохранён (label={}) для user={} биржа={} режим={}",
                label, user.getTelegramUserId(),
                exchange, testMode ? "TESTNET" : "REAL");
    }

    @Override
    @Transactional
    public void selectCredential(User user, String exchange, boolean testMode, String label) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        List<ApiCredentials> all = repo.findAllBySettings(settings);

        all.forEach(c -> c.setActive(c.getLabel().equals(label)));
        repo.saveAll(all);

        log.info("✅ Активирован ключ '{}' для user={} биржа={} режим={}",
                label, user.getTelegramUserId(),
                exchange, testMode ? "TESTNET" : "REAL");
    }

    @Override
    public boolean testConnection(User user, String exchange, boolean testMode) {
        AiTradeSettings settings = settingsService.getOrCreate(user);
        String label = exchange + (testMode ? "_testnet" : "_main");

        ApiCredentials active = repo.findBySettingsAndLabel(settings, label)
                .filter(ApiCredentials::isActive)
                .orElseThrow(() -> new IllegalStateException("Активные ключи не найдены"));

        try {
            BinanceApiClient client = clientFactory.create(
                    active.getApiKey(), active.getApiSecret(), testMode
            );
            client.getAccountInfo();
            return true;
        } catch (Exception e) {
            log.error("❌ Ошибка подключения к Binance биржа={} режим={} — {}",
                    exchange,
                    testMode ? "TESTNET" : "REAL",
                    e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<ApiCredentials> listAllForSettings(AiTradeSettings settings) {
        return List.of();
    }
}
