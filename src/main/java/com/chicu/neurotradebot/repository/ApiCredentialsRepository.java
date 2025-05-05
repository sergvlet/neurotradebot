// src/main/java/com/chicu/neurotradebot/repository/ApiCredentialsRepository.java
package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiCredentialsRepository extends JpaRepository<ApiCredentials, Long> {
    List<ApiCredentials> findAllBySettings(AiTradeSettings settings);
    Optional<ApiCredentials> findBySettingsAndLabel(AiTradeSettings settings, String label);

}
