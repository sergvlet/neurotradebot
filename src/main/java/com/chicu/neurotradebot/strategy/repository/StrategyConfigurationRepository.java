package com.chicu.neurotradebot.strategy.repository;

import com.chicu.neurotradebot.strategy.entity.StrategyConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StrategyConfigurationRepository extends JpaRepository<StrategyConfiguration, Long> {
    List<StrategyConfiguration> findByUserId(Long userId);
}
