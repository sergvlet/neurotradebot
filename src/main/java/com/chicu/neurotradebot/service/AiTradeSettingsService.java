package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.model.AiTradeSettings;
import com.chicu.neurotradebot.model.UserTradingSettings;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.repository.UserTradingSettingsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsService {

    private final AiTradeSettingsRepository aiTradeSettingsRepository;
    private final UserTradingSettingsRepository tradingSettingsRepository;

    /**
     * Найти AI-торговые настройки по ID пользователя.
     */
    public Optional<AiTradeSettings> findByUserId(Long userId) {
        return aiTradeSettingsRepository.findById(userId);
    }

    /**
     * Создать AI-настройки с дефолтными значениями, если ещё не существуют.
     */
    @Transactional
    public AiTradeSettings createIfAbsentAndInitializeDefaults(Long userId) {
        // Если уже существует — обновим недостающие поля и вернём
        Optional<AiTradeSettings> existing = aiTradeSettingsRepository.findById(userId);
        if (existing.isPresent()) {
            AiTradeSettings ai = existing.get();
            initializeDefaultsIfNull(ai);
            return aiTradeSettingsRepository.save(ai);
        }

        // Грузим торговые настройки без повторного создания User
        UserTradingSettings tradingSettings = tradingSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("UserTradingSettings not found for user " + userId));

        AiTradeSettings ai = new AiTradeSettings();
        ai.setUserTradingSettings(tradingSettings);
        ai.setStrategy("RSI_EMA");
        ai.setRisk("medium");
        ai.setTradingType("spot");
        ai.setNotifications(true);
        ai.setPairMode("MANUAL");
        ai.setManualPair("BTC/USDT");
        ai.setAllowedPairs("");
        ai.setRunning(false);

        initializeDefaultsIfNull(ai);

        // ВАЖНО: установить в tradingSettings, НО НЕ трогать User
        tradingSettings.setAiTradeSettings(ai);

        // Сохраняем только AiTradeSettings — User уже есть
        return aiTradeSettingsRepository.save(ai);
    }

    /**
     * Безопасная инициализация параметров стратегии по умолчанию.
     */
    public void initializeDefaultsIfNull(AiTradeSettings settings) {
        if (settings.getMinCandles() == null) settings.setMinCandles(50);
        if (settings.getEmaShort() == null) settings.setEmaShort(9);
        if (settings.getEmaLong() == null) settings.setEmaLong(21);
        if (settings.getRsiPeriod() == null) settings.setRsiPeriod(14);
        if (settings.getRsiBuyThreshold() == null) settings.setRsiBuyThreshold(30.0);
        if (settings.getRsiSellThreshold() == null) settings.setRsiSellThreshold(70.0);
    }

    /**
     * Сохранить или обновить AI-торговые настройки.
     */
    public void saveOrUpdate(AiTradeSettings settings) {
        aiTradeSettingsRepository.save(settings);
    }
}
