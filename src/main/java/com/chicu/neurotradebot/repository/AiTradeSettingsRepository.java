package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.AiTradeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с AI-торговыми настройками пользователей.
 */
@Repository
public interface AiTradeSettingsRepository extends JpaRepository<AiTradeSettings, Long> {

    List<AiTradeSettings> findByRunningTrue();

}
