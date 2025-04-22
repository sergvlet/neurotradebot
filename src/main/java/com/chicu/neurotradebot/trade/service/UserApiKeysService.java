package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.model.UserApiKeys;
import com.chicu.neurotradebot.trade.repository.UserApiKeysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApiKeysService {

    private final UserApiKeysRepository repository;

    public UserApiKeys getOrCreate(Long chatId, Exchange exchange) {
        return repository.findByChatIdAndExchange(chatId, exchange)
                .orElseGet(() -> {
                    UserApiKeys keys = new UserApiKeys();
                    keys.setChatId(chatId);
                    keys.setExchange(exchange);
                    return repository.save(keys);
                });
    }

    public void saveRealKeys(Long chatId, Exchange exchange, String key, String secret) {
        UserApiKeys keys = getOrCreate(chatId, exchange);
        keys.setRealApiKey(key);
        keys.setRealApiSecret(secret);
        repository.save(keys);
    }

    public void saveTestKeys(Long chatId, Exchange exchange, String key, String secret) {
        UserApiKeys keys = getOrCreate(chatId, exchange);
        keys.setTestApiKey(key);
        keys.setTestApiSecret(secret);
        repository.save(keys);
    }

    public boolean hasRealKeys(Long chatId, Exchange exchange) {
        return getOrCreate(chatId, exchange).hasRealKeys();
    }

    public boolean hasTestKeys(Long chatId, Exchange exchange) {
        return getOrCreate(chatId, exchange).hasTestKeys();
    }

    public Optional<UserApiKeys> find(Long chatId, Exchange exchange) {
        return repository.findByChatIdAndExchange(chatId, exchange);
    }
}
