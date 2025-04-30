package com.chicu.neurotradebot.repository;

import java.util.Optional;
import com.chicu.neurotradebot.model.UserTradingSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTradingSettingsRepository extends JpaRepository<UserTradingSettings, Long> {

    Optional<UserTradingSettings> findByUserId(Long userId); // 🔧 вот это нужно
}
