package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    Optional<UserSettings> findByChatId(Long chatId);
}
