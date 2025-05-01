package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.StrategySettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StrategySettingsRepository extends JpaRepository<StrategySettings, Long> {
    Optional<StrategySettings> findByUserId(Long userId);
}
