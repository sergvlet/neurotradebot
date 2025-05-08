// src/main/java/com/chicu/neurotradebot/service/impl/AiTradeSettingsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.enums.ConfigWaiting;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsServiceImpl implements AiTradeSettingsService {

    private final AiTradeSettingsRepository repo;
    private final UserService userService;

    /** Хранилище текущего состояния ожидания от каждого chatId */
    private final Map<Long, ConfigWaiting> waitingMap = new ConcurrentHashMap<>();

    @Override
    public AiTradeSettings getOrCreate(User user) {
        return repo.findByUser(user)
                .orElseGet(() -> {
                    RsiMacdConfig defaultRsiMacd = RsiMacdConfig.builder()
                            .rsiPeriod(14)
                            .rsiLower(BigDecimal.valueOf(30))
                            .rsiUpper(BigDecimal.valueOf(70))
                            .macdFast(12)
                            .macdSlow(26)
                            .macdSignal(9)
                            .build();
                    AiTradeSettings s = AiTradeSettings.builder()
                            .user(user)
                            .rsiMacdConfig(defaultRsiMacd)
                            .build();
                    return repo.save(s);
                });
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

    // === Методы для механизма ожидания ввода параметров RSI ===

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
        AiTradeSettings cfg = getByChatId(chatId);
        Set<StrategyType> s = cfg.getStrategies();
        if (s.contains(type)) s.remove(type);
        else                  s.add(type);
        repo.save(cfg);
    }


}
