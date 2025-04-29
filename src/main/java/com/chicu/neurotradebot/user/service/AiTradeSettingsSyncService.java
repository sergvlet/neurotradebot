package com.chicu.neurotradebot.user.service;

import com.chicu.neurotradebot.user.entity.AiTradeSettings;
import com.chicu.neurotradebot.user.entity.UserTradingSettings;
import com.chicu.neurotradebot.user.repository.UserTradingSettingsRepository;
import com.chicu.neurotradebot.telegram.session.UserSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для синхронизации настроек AI между базой данных и сессией Telegram.
 */
@Service
@RequiredArgsConstructor
public class AiTradeSettingsSyncService {

    private final AiTradeSettingsService aiTradeSettingsService;
    private final UserTradingSettingsRepository userTradingSettingsRepository;

    /**
     * Загружает настройки AI из базы и сохраняет их в UserSessionManager.
     */
    public void loadSettingsToSession(Long userId) {
        aiTradeSettingsService.findByUserId(userId).ifPresent(settings -> {
            UserSessionManager.setAiStrategy(userId, settings.getStrategy());
            UserSessionManager.setAiRiskLevel(userId, settings.getRisk());
            UserSessionManager.setAiTradingType(userId, settings.getTradingType());
            UserSessionManager.setAiAutostart(userId, Boolean.TRUE.equals(settings.getAutostart()));
            UserSessionManager.setAiNotifications(userId, Boolean.TRUE.equals(settings.getNotifications()));
            UserSessionManager.setAiPairMode(userId, settings.getPairMode());
            UserSessionManager.setAiManualPair(userId, settings.getManualPair());
            UserSessionManager.setAiAllowedPairs(userId, settings.getAllowedPairs());

        });
    }

    /**
     * Сохраняет текущие значения из UserSessionManager в базу.
     */
    public void saveSessionToDb(Long userId) {
        Optional<UserTradingSettings> userSettingsOpt = userTradingSettingsRepository.findById(userId);
        if (userSettingsOpt.isEmpty()) return;

        UserTradingSettings userSettings = userSettingsOpt.get();
        AiTradeSettings ai = userSettings.getAiTradeSettings();

        if (ai == null) {
            ai = new AiTradeSettings();
            ai.setUserTradingSettings(userSettings);
            userSettings.setAiTradeSettings(ai);
        }

        ai.setStrategy(UserSessionManager.getAiStrategy(userId));
        ai.setRisk(UserSessionManager.getAiRiskLevel(userId));
        ai.setTradingType(UserSessionManager.getAiTradingType(userId));
        ai.setAutostart(UserSessionManager.isAiAutostart(userId));
        ai.setNotifications(UserSessionManager.isAiNotifications(userId));
        ai.setPairMode(UserSessionManager.getAiPairMode(userId));
        ai.setManualPair(UserSessionManager.getAiManualPair(userId));
        ai.setAllowedPairs(UserSessionManager.getAiAllowedPairs(userId));

        aiTradeSettingsService.saveOrUpdate(ai);
    }
}
