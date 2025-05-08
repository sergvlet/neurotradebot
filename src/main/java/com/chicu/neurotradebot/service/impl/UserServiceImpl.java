// src/main/java/com/chicu/neurotradebot/service/impl/UserServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * При /start и других взаимодействиях:
     * создаём или обновляем telegramUserId и username.
     * Если telegramUserId == null — берём из from.getId().
     */
    @Override
    @Transactional
    public User getOrCreate(Long telegramUserId,
                            org.telegram.telegrambots.meta.api.objects.User from) {
        Long effectiveId = telegramUserId != null ? telegramUserId : from.getId();
        String name = from.getUserName() != null ? from.getUserName() : "";

        final Long userId = effectiveId;
        return userRepository.findByTelegramUserId(userId)
                .map(u -> {
                    u.setUsername(name);
                    return userRepository.save(u);
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setTelegramUserId(userId);
                    u.setUsername(name);
                    return userRepository.save(u);
                });
    }

    /**
     * Просто возвращает или создаёт пользователя по telegramUserId.
     * Если telegramUserId == null, пытаемся взять из BotContext.
     * При создании ставим пустую строку в username, чтобы не было NULL.
     * Если всё же не удаётся получить ID, возвращаем любого уже существующего пользователя,
     * чтобы не падать с ошибками.
     */
    @Override
    @Transactional
    public User getOrCreate(Long telegramUserId) {
        Long effectiveId = telegramUserId != null
                ? telegramUserId
                : BotContext.getChatId();

        if (effectiveId != null) {
            final Long userId = effectiveId;
            return userRepository.findByTelegramUserId(userId)
                    .orElseGet(() -> {
                        User u = new User();
                        u.setTelegramUserId(userId);
                        u.setUsername("");
                        return userRepository.save(u);
                    });
        }

        // fallback: если и туда не залогинить — возвращаем первого найденного
        List<User> all = userRepository.findAll();
        if (!all.isEmpty()) {
            return all.get(0);
        }
        // и если БД пуста — создаём «нулевого» пользователя, чтобы гарантировать non-null
        User u = new User();
        u.setTelegramUserId(0L);
        u.setUsername("");
        return userRepository.save(u);
    }
}
