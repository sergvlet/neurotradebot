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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsServiceImpl implements AiTradeSettingsService {

    private final AiTradeSettingsRepository repo;
    private final UserService userService;

    private final Map<Long, ConfigWaiting> waitingMap = new ConcurrentHashMap<>();

    @Override
    public AiTradeSettings getOrCreate(User user) {
        AiTradeSettings settings = repo.findByUser(user)
            .orElseGet(() -> repo.save(AiTradeSettings.builder().user(user).build()));

        boolean changed = false;

        if (settings.getRsiConfig() == null) {
            RsiConfig r = new RsiConfig();
            r.setSettings(settings);
            settings.setRsiConfig(r);
            changed = true;
        }

        if (settings.getMacdConfig() == null) {
            MacdConfig m = new MacdConfig();
            m.setSettings(settings);
            settings.setMacdConfig(m);
            changed = true;
        }

        if (settings.getEmaCrossoverConfig() == null) {
            EmaCrossoverConfig e = new EmaCrossoverConfig();
            e.setSettings(settings);
            settings.setEmaCrossoverConfig(e);
            changed = true;
        }

        if (settings.getBollingerConfig() == null) {
            BollingerConfig b = new BollingerConfig();
            b.setSettings(settings);
            settings.setBollingerConfig(b);
            changed = true;
        }

        if (settings.getDcaConfig() == null) {
            DcaConfig d = new DcaConfig();
            d.setSettings(settings);
            settings.setDcaConfig(d);
            changed = true;
        }

        if (settings.getScalpingConfig() == null) {
            ScalpingConfig s = new ScalpingConfig();
            s.setSettings(settings);
            settings.setScalpingConfig(s);
            changed = true;
        }

        if (changed) {
            settings = repo.save(settings);
        }
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
}
