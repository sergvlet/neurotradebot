// src/main/java/com/chicu/neurotradebot/service/impl/AiTradeSettingsServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                    // При первом создании задаём стратегию RSI+MACD с дефолтными параметрами
                    RsiMacdConfig defaultRsiMacd = RsiMacdConfig.builder()
                            // пример изменения дефолта, если нужно
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
    public void save(AiTradeSettings settings) {
        repo.save(settings);
    }

    @Override
    public AiTradeSettings getForCurrentUser() {
        Long chatId = com.chicu.neurotradebot.telegram.BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        return getOrCreate(user);
    }

    @Override
    public List<AiTradeSettings> findAllActive() {
        return repo.findByEnabledTrue();
    }

    @Override
    public AiTradeSettings getByChatId(Long chatId) {
        User user = userService.getOrCreate(chatId);
        return getOrCreate(user);
    }
}
