package com.chicu.neurotradebot.user.service;

import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.entity.UserTradingSettings;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.user.repository.UserTradingSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradingSettingsService {

    private final UserRepository userRepository;
    private final UserTradingSettingsRepository settingsRepository;

    public UserTradingSettings getOrCreateSettings(Long userId) {
        return settingsRepository.findById(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
                    UserTradingSettings settings = new UserTradingSettings();
                    settings.setUser(user);
                    settings.setExchange("Не выбрана");
                    settings.setUseTestnet(true); // По умолчанию тестовая сеть
                    settings.setTradingMode("MANUAL"); // По умолчанию ручной режим
                    return settingsRepository.save(settings);
                });
    }

    public void updateExchange(Long userId, String exchange) {
        UserTradingSettings settings = getOrCreateSettings(userId);
        settings.setExchange(exchange);
        settingsRepository.save(settings);
    }

    public void toggleTestnet(Long userId) {
        UserTradingSettings settings = getOrCreateSettings(userId);
        settings.setUseTestnet(!settings.getUseTestnet());
        settingsRepository.save(settings);
    }

    public void updateTradingMode(Long userId, String mode) {
        UserTradingSettings settings = getOrCreateSettings(userId);
        settings.setTradingMode(mode);
        settingsRepository.save(settings);
    }
}
