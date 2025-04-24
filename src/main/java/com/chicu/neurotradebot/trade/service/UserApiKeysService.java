package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.UserApiKeys;
import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.repository.UserApiKeysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserApiKeysService {

    private final UserApiKeysRepository userApiKeysRepository;

    // Получение ключей пользователя по chatId
    public UserApiKeys getUserApiKeys(Long chatId) {
        return userApiKeysRepository.findByChatId(chatId);
    }

    // Получение или создание ключей пользователя по chatId и Exchange
    public UserApiKeys getOrCreate(Long chatId, Exchange exchange) {
        UserApiKeys keys = userApiKeysRepository.findByChatIdAndExchange(chatId, exchange);

        // Если ключи не найдены, создаем новые
        if (keys == null) {
            keys = new UserApiKeys();
            keys.setChatId(chatId);
            keys.setExchange(exchange);
            userApiKeysRepository.save(keys);
        }
        return keys;
    }

    // Сохранение тестовых ключей
    public void saveTestKeys(Long chatId, Exchange exchange, String apiKey, String apiSecret) {
        UserApiKeys keys = getOrCreate(chatId, exchange);
        keys.setTestApiKey(apiKey);
        keys.setTestApiSecret(apiSecret);
        userApiKeysRepository.save(keys);
    }

    // Сохранение реальных ключей
    public void saveRealKeys(Long chatId, Exchange exchange, String apiKey, String apiSecret) {
        UserApiKeys keys = getOrCreate(chatId, exchange);
        keys.setRealApiKey(apiKey);
        keys.setRealApiSecret(apiSecret);
        userApiKeysRepository.save(keys);
    }

    // Проверка наличия тестовых ключей
    public boolean hasTestKeys(Long chatId, Exchange exchange) {
        UserApiKeys keys = getOrCreate(chatId, exchange);
        return keys.hasTestKeys();
    }

    // Проверка наличия реальных ключей
    public boolean hasRealKeys(Long chatId, Exchange exchange) {
        UserApiKeys keys = getOrCreate(chatId, exchange);
        return keys.hasRealKeys();
    }

    // Получение API ключа для пользователя (реальный или тестовый)
    public String getApiKey(Long chatId, boolean useRealKeys) {
        UserApiKeys keys = getUserApiKeys(chatId);
        if (keys == null) {
            throw new RuntimeException("API ключи не найдены для пользователя");
        }

        if (useRealKeys && keys.hasRealKeys()) {
            return keys.getRealApiKey();
        } else if (!useRealKeys && keys.hasTestKeys()) {
            return keys.getTestApiKey();
        } else {
            throw new RuntimeException("Невозможно найти подходящие ключи для пользователя");
        }
    }

    // Получение API секрета для пользователя (реальный или тестовый)
    public String getApiSecret(Long chatId, boolean useRealKeys) {
        UserApiKeys keys = getUserApiKeys(chatId);
        if (keys == null) {
            throw new RuntimeException("API секрет не найден для пользователя");
        }

        if (useRealKeys && keys.hasRealKeys()) {
            return keys.getRealApiSecret();
        } else if (!useRealKeys && keys.hasTestKeys()) {
            return keys.getTestApiSecret();
        } else {
            throw new RuntimeException("Невозможно найти подходящий секретный ключ для пользователя");
        }
    }
}
