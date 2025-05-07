// src/main/java/com/chicu/neurotradebot/service/impl/AiTradeSettingsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsServiceImpl implements AiTradeSettingsService {

    private final AiTradeSettingsRepository repo;
    private final UserService userService;

    @Override
    public AiTradeSettings getOrCreate(User user) {
        return repo.findByUser(user)
                .orElseGet(() -> {
                    AiTradeSettings s = AiTradeSettings.builder()
                            .user(user)
                            .build();
                    return repo.save(s);
                });
    }

    @Override
    public void save(AiTradeSettings settings) {
        repo.save(settings);
    }

    @Override
    public AiTradeSettings getForCurrentUser() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        return getOrCreate(user);
    }

    @Override
    public List<AiTradeSettings> findAllActive() {
        return repo.findByEnabledTrue();
    }

    @Override
    public AiTradeSettings getByChatId(Long chatId) {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId must not be null");
        }
        User user = userService.getOrCreate(chatId);
        return getOrCreate(user);
    }
}
