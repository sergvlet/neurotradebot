package com.chicu.neurotradebot.user.repository;

import com.chicu.neurotradebot.user.entity.ExchangeSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeSettingsRepository extends JpaRepository<ExchangeSettings, Long> {


    Optional<ExchangeSettings> findByUserIdAndExchange(Long userId, String exchange); // Оставляем старый на всякий случай
}
