package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.model.AiTradeSettings;
import com.chicu.neurotradebot.model.User;
import com.chicu.neurotradebot.model.UserTradingSettings;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.repository.UserTradingSettingsRepository;
import com.chicu.neurotradebot.session.UserSessionManager;
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
            aiTradeSettingsService.initializeDefaultsIfNull(settings); // 👈 гарантия заполнения

            UserSessionManager.setAiStrategy(userId, settings.getStrategy());
            UserSessionManager.setAiRiskLevel(userId, settings.getRisk());
            UserSessionManager.setAiTradingType(userId, settings.getTradingType());
            UserSessionManager.setAiNotifications(userId, Boolean.TRUE.equals(settings.getNotifications()));
            UserSessionManager.setAiPairMode(userId, settings.getPairMode());
            UserSessionManager.setAiManualPair(userId, settings.getManualPair());
            UserSessionManager.setAiRunning(userId, Boolean.TRUE.equals(settings.getRunning()));

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
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return;
        User user = userOpt.get();

        UserTradingSettings tradingSettings = user.getTradingSettings();
        if (tradingSettings == null) {
            tradingSettings = new UserTradingSettings();
            tradingSettings.setUser(user);
            user.setTradingSettings(tradingSettings);
        }

        AiTradeSettings ai = tradingSettings.getAiTradeSettings();
        if (ai == null) {
            ai = new AiTradeSettings();
            ai.setUserTradingSettings(tradingSettings);
            tradingSettings.setAiTradeSettings(ai);
        }

        ai.setStrategy(UserSessionManager.getAiStrategy(userId));
        ai.setRisk(UserSessionManager.getAiRiskLevel(userId));
        ai.setTradingType(UserSessionManager.getAiTradingType(userId));
        ai.setNotifications(UserSessionManager.isAiNotifications(userId));
        ai.setPairMode(UserSessionManager.getAiPairMode(userId));
        ai.setManualPair(UserSessionManager.getAiManualPair(userId));
        ai.setAllowedPairs(String.join("\n", UserSessionManager.getAiAllowedPairsList(userId)));
        ai.setRunning(UserSessionManager.isAiRunning(userId));

        aiTradeSettingsService.initializeDefaultsIfNull(ai); // 👈 защита перед сохранением

        userRepository.save(user);
    }

    public void setAiRunning(Long userId, boolean running) {
        UserSessionManager.setAiRunning(userId, running);
        saveSessionToDb(userId);
    }
}
