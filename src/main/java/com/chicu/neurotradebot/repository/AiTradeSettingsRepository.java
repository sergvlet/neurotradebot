package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.AiTradeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с AI-торговыми настройками пользователей.
 */
@Repository
public interface AiTradeSettingsRepository extends JpaRepository<AiTradeSettings, Long> {
    // По умолчанию есть метод findById(userId)
}
