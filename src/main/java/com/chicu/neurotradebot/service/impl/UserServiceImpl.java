// src/main/java/com/chicu/neurotradebot/service/impl/UserServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация UserService, создаёт/обновляет и возвращает пользователя.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * При /start: создаём или обновляем telegramUserId и username.
     */
    @Override
    @Transactional
    public User getOrCreate(Long telegramUserId,
                            org.telegram.telegrambots.meta.api.objects.User from) {
        return userRepository.findByTelegramUserId(telegramUserId)
                .map(u -> {
                    String name = from.getUserName() != null ? from.getUserName() : "";
                    u.setUsername(name);
                    return userRepository.save(u);
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setTelegramUserId(telegramUserId);
                    String name = from.getUserName() != null ? from.getUserName() : "";
                    u.setUsername(name);
                    return userRepository.save(u);
                });
    }

    /**
     * Просто возвращает или создаёт пользователя по telegramUserId.
     * При создании ставим пустую строку в username, чтобы не было NULL.
     */
    @Override
    @Transactional
    public User getOrCreate(Long telegramUserId) {
        return userRepository.findByTelegramUserId(telegramUserId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setTelegramUserId(telegramUserId);
                    u.setUsername("");    // пустая строка вместо null
                    return userRepository.save(u);
                });
    }
}
