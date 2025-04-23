package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.StrategyConfigEntity;
import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategyConfigRepository extends JpaRepository<StrategyConfigEntity, Long> {
    Optional<StrategyConfigEntity> findByChatIdAndStrategy(Long chatId, AvailableStrategy strategy);
}
