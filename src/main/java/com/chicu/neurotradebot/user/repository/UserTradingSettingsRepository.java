package com.chicu.neurotradebot.user.repository;

import com.chicu.neurotradebot.user.entity.UserTradingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTradingSettingsRepository extends JpaRepository<UserTradingSettings, Long> {
}
