// src/main/java/com/chicu/neurotradebot/service/impl/AiTradeSettingsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsServiceImpl implements AiTradeSettingsService {

    private final AiTradeSettingsRepository repo;
    private final UserService userService;

    /**
     * Получить существующую конфигурацию или создать новую.
     * При создании выставляем только пользователя и шаг настройки API = NONE.
     */
    @Override
    @Transactional
    public AiTradeSettings getOrCreate(User user) {
        return repo.findByUser(user)
                .orElseGet(() -> {
                    AiTradeSettings s = AiTradeSettings.builder()
                            .user(user)
                            // exchange оставляем null до выбора
                            .exchange(null)
                            // testMode/aiEnabled по умолчанию false благодаря @PrePersist
                            .apiSetupStep(ApiSetupStep.NONE)
                            .build();
                    return repo.save(s);
                });
    }

    /**
     * Сохраняет изменения в настройках.
     */
    @Override
    @Transactional
    public void save(AiTradeSettings settings) {
        repo.save(settings);
    }

    /**
     * Получить настройки для "текущего" пользователя (из BotContext).
     */
    @Override
    public AiTradeSettings getForCurrentUser() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        return getOrCreate(user);
    }
}
