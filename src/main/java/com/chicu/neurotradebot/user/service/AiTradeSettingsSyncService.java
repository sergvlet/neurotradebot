package com.chicu.neurotradebot.user.service;

import com.chicu.neurotradebot.user.entity.AiTradeSettings;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.entity.UserTradingSettings;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.user.repository.UserTradingSettingsRepository;
import com.chicu.neurotradebot.telegram.session.UserSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiTradeSettingsSyncService {

    private final AiTradeSettingsService aiTradeSettingsService;
    private final UserTradingSettingsRepository userTradingSettingsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void loadSettingsToSession(Long userId) {
        aiTradeSettingsService.findByUserId(userId).ifPresent(settings -> {
            UserSessionManager.setAiStrategy(userId, settings.getStrategy());
            UserSessionManager.setAiRiskLevel(userId, settings.getRisk());
            UserSessionManager.setAiTradingType(userId, settings.getTradingType());
            UserSessionManager.setAiAutostart(userId, Boolean.TRUE.equals(settings.getAutostart()));
            UserSessionManager.setAiNotifications(userId, Boolean.TRUE.equals(settings.getNotifications()));
            UserSessionManager.setAiPairMode(userId, settings.getPairMode());
            UserSessionManager.setAiManualPair(userId, settings.getManualPair());

            UserSessionManager.clearAiAllowedPairsList(userId);
            String allowed = settings.getAllowedPairs();
            if (allowed != null && !allowed.isBlank()) {
                for (String line : allowed.split("\n")) {
                    if (!line.isBlank()) {
                        UserSessionManager.appendAiAllowedPair(userId, line.trim());
                    }
                }
            }
        });
    }

    @Transactional
    public void saveSessionToDb(Long userId) {
        // Ищем пользователя
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return;
        User user = userOpt.get();

        // Получаем или создаём торговые настройки
        UserTradingSettings tradingSettings = user.getTradingSettings();
        if (tradingSettings == null) {
            tradingSettings = new UserTradingSettings();
            tradingSettings.setUser(user);
            user.setTradingSettings(tradingSettings);
        }

        // Получаем или создаём AI-настройки
        AiTradeSettings ai = tradingSettings.getAiTradeSettings();
        if (ai == null) {
            ai = new AiTradeSettings();
            ai.setUserTradingSettings(tradingSettings);
            tradingSettings.setAiTradeSettings(ai);
        }

        ai.setStrategy(UserSessionManager.getAiStrategy(userId));
        ai.setRisk(UserSessionManager.getAiRiskLevel(userId));
        ai.setTradingType(UserSessionManager.getAiTradingType(userId));
        ai.setAutostart(UserSessionManager.isAiAutostart(userId));
        ai.setNotifications(UserSessionManager.isAiNotifications(userId));
        ai.setPairMode(UserSessionManager.getAiPairMode(userId));
        ai.setManualPair(UserSessionManager.getAiManualPair(userId));
        ai.setAllowedPairs(String.join("\n", UserSessionManager.getAiAllowedPairsList(userId)));

        // Сохраняем через каскад — достаточно сохранить User
        userRepository.save(user);
    }
}
