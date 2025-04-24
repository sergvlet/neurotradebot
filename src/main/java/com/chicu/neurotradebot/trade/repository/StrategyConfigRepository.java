package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.StrategyConfigEntity;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StrategyConfigRepository extends JpaRepository<StrategyConfigEntity, Long> {
    StrategyConfigEntity findByUserSettingsAndStrategy(UserSettings userSettings, AvailableStrategy strategy);
}
