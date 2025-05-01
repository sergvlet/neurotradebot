package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.model.StrategySettings;
import com.chicu.neurotradebot.model.User;
import com.chicu.neurotradebot.repository.StrategySettingsRepository;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.strategy.RsiEmaStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategySettingsService {

    private final StrategySettingsRepository strategySettingsRepository;
    private final UserRepository userRepository;

    public RsiEmaStrategy loadStrategy(Long userId) {
        StrategySettings settings = strategySettingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // ✅ Загружаем user из базы (не создаём new User)
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

                    StrategySettings s = new StrategySettings();
                    s.setUser(user);
                    s.setStrategyName("RSI_EMA");
                    s.resetToDefault();

                    return strategySettingsRepository.save(s);
                });

        return toStrategy(settings);
    }

    public void saveStrategy(Long userId, RsiEmaStrategy strategy) {
        StrategySettings settings = strategySettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Strategy settings not found"));

        settings.setEmaShort(strategy.getEmaShort());
        settings.setEmaLong(strategy.getEmaLong());
        settings.setRsiPeriod(strategy.getRsiPeriod());
        settings.setRsiBuyThreshold(strategy.getRsiBuyThreshold());
        settings.setRsiSellThreshold(strategy.getRsiSellThreshold());
        settings.setMinCandles(strategy.getMinCandles());

        strategySettingsRepository.save(settings);
    }

    public void resetToDefault(Long userId) {
        StrategySettings settings = strategySettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Strategy settings not found"));
        settings.resetToDefault();
        strategySettingsRepository.save(settings);
    }

    private RsiEmaStrategy toStrategy(StrategySettings s) {
        RsiEmaStrategy strategy = new RsiEmaStrategy();
        strategy.setEmaShort(s.getEmaShort());
        strategy.setEmaLong(s.getEmaLong());
        strategy.setRsiPeriod(s.getRsiPeriod());
        strategy.setRsiBuyThreshold(s.getRsiBuyThreshold());
        strategy.setRsiSellThreshold(s.getRsiSellThreshold());
        strategy.setMinCandles(s.getMinCandles());
        return strategy;
    }
}
