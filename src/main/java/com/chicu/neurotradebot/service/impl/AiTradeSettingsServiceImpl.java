// src/main/java/com/chicu/neurotradebot/service/impl/AiTradeSettingsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.*;
import com.chicu.neurotradebot.enums.ConfigWaiting;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsServiceImpl implements AiTradeSettingsService {

    private final AiTradeSettingsRepository repo;
    private final UserService userService;

    private final Map<Long, ConfigWaiting> waitingMap = new ConcurrentHashMap<>();

    /**
     * Дефолтный конфиг ML TP/SL
     */
    private static final MlStrategyConfig DEFAULT_ML = MlStrategyConfig.builder()
            .totalCapitalUsd(BigDecimal.valueOf(100))
            .entryRsiThreshold(32.0)
            .lookbackPeriod(Duration.ofDays(30))
            .predictUrl("http://localhost:5000/predict")
            .build();

    @Override
    public AiTradeSettings getOrCreate(User user) {
        AiTradeSettings settings = repo.findByUser(user)
                .orElseGet(() -> repo.save(AiTradeSettings.builder().user(user).build()));
        // ... инициализация остальных вложенных конфигов без изменений ...
        return settings;
    }

    @Override
    public AiTradeSettings getByChatId(Long chatId) {
        User user = userService.getOrCreate(chatId);
        return getOrCreate(user);
    }

    @Override
    public AiTradeSettings getForCurrentUser() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        return getOrCreate(user);
    }

    @Override
    public void save(AiTradeSettings settings) {
        repo.save(settings);
    }

    @Override
    public List<AiTradeSettings> findAllActive() {
        return repo.findByEnabledTrue();
    }

    @Override
    public void markWaiting(Long chatId, ConfigWaiting what) {
        waitingMap.put(chatId, what);
    }

    @Override
    public ConfigWaiting getWaiting(Long chatId) {
        return waitingMap.get(chatId);
    }

    @Override
    public void clearWaiting(Long chatId) {
        waitingMap.remove(chatId);
    }

    @Override
    public void toggleStrategy(Long chatId, StrategyType type) {
        AiTradeSettings settings = getByChatId(chatId);
        Set<StrategyType> st = settings.getStrategies();
        if (st.contains(type)) st.remove(type);
        else                    st.add(type);
        repo.save(settings);
    }

    /**
     * Переключает флаг ML TP/SL в настройках пользователя.
     */
    @Override
    @Transactional
    public void toggleMlTpSl(Long chatId) {
        AiTradeSettings settings = getByChatId(chatId);
        settings.setUseMlTpSl(!settings.isUseMlTpSl());
        repo.save(settings);
    }

    @Override
    @Transactional
    public void updateMlTotalCapital(Long chatId, double deltaUsd) {
        AiTradeSettings s = getByChatId(chatId);
        MlStrategyConfig c = s.getMlStrategyConfig();
        c.setTotalCapitalUsd(c.getTotalCapitalUsd().add(BigDecimal.valueOf(deltaUsd)));
        repo.save(s);
    }

    @Override
    @Transactional
    public void updateMlEntryRsiThreshold(Long chatId, double deltaRsi) {
        AiTradeSettings s = getByChatId(chatId);
        MlStrategyConfig c = s.getMlStrategyConfig();
        c.setEntryRsiThreshold(c.getEntryRsiThreshold() + deltaRsi);
        repo.save(s);
    }

    @Override
    @Transactional
    public void updateMlLookbackPeriod(Long chatId, int deltaHours) {
        AiTradeSettings s = getByChatId(chatId);
        MlStrategyConfig c = s.getMlStrategyConfig();
        c.setLookbackPeriod(c.getLookbackPeriod().plusHours(deltaHours));
        repo.save(s);
    }

    @Override
    @Transactional
    public void resetMlConfig(Long chatId) {
        AiTradeSettings s = getByChatId(chatId);
        // вместо прежнего DEFAULT обнуляем конфиг
        s.setMlStrategyConfig(DEFAULT_ML);
        repo.save(s);
    }
}
