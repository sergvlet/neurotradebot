package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User getOrCreate(Long telegramUserId,
                            org.telegram.telegrambots.meta.api.objects.User from) {
        return userRepository.findByTelegramUserId(telegramUserId)
            .map(u -> {
                // обновляем поля из Telegram API
                u.setUsername(from.getUserName());
                u.setFirstName(from.getFirstName());
                u.setLastName(from.getLastName());
                u.setLanguageCode(from.getLanguageCode());
                return userRepository.save(u);
            })
            .orElseGet(() -> {
                // создаём нового с полями из Telegram API
                User u = User.builder()
                        .telegramUserId(telegramUserId)
                    .username(from.getUserName())
                    .firstName(from.getFirstName())
                    .lastName(from.getLastName())
                    .languageCode(from.getLanguageCode())
                    .build();
                return userRepository.save(u);
            });
    }

    @Override
    @Transactional
    public User getOrCreate(Long telegramUserId) {
        return userRepository.findByTelegramUserId(telegramUserId)
                .orElseGet(() -> {
                    User u = User.builder()
                            .telegramUserId(telegramUserId)
                            .build();
                    return userRepository.save(u);
                });
    }

    @Override
    @Transactional
    public User updatePhoneNumber(Long telegramUserId, String phoneNumber) {
        User u = userRepository.findByTelegramUserId(telegramUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + telegramUserId));
        u.setPhoneNumber(phoneNumber);
        return userRepository.save(u);
    }
}
