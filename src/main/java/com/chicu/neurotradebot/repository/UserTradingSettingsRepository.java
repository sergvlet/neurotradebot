package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.UserTradingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTradingSettingsRepository extends JpaRepository<UserTradingSettings, Long> {
}
