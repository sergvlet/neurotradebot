// src/main/java/com/chicu/neurotradebot/repository/AiTradeSettingsRepository.java
package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiTradeSettingsRepository extends JpaRepository<AiTradeSettings, Long> {
    Optional<AiTradeSettings> findByUser(User user);
}
