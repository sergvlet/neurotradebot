package com.chicu.neurotradebot.user.service;

import com.chicu.neurotradebot.user.entity.AiTradeSettings;
import com.chicu.neurotradebot.user.repository.AiTradeSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для управления AI-торговыми настройками пользователя.
 */
@Service
@RequiredArgsConstructor
public class AiTradeSettingsService {

    private final AiTradeSettingsRepository aiTradeSettingsRepository;

    /**
     * Найти AI-торговые настройки по ID пользователя.
     * 
     * @param userId Telegram ID пользователя
     * @return Optional с найденными настройками или пустой
     */
    public Optional<AiTradeSettings> findByUserId(Long userId) {
        return aiTradeSettingsRepository.findById(userId);
    }

    /**
     * Сохранить или обновить AI-торговые настройки.
     *
     * @param settings Объект настроек
     */
    public void saveOrUpdate(AiTradeSettings settings) {
        aiTradeSettingsRepository.save(settings); // ← Обязательно!
    }

}
