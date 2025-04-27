package com.chicu.neurotradebot.user.service;

import com.chicu.neurotradebot.user.entity.ExchangeSettings;
import com.chicu.neurotradebot.user.repository.ExchangeSettingsRepository;
import com.chicu.neurotradebot.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeSettingsService {

    private final ExchangeSettingsRepository exchangeSettingsRepository;
    private final UserRepository userRepository;

    public boolean isTestnetEnabled(Long userId, String exchange) {
        return exchangeSettingsRepository.findByUserIdAndExchange(userId, exchange)
                .map(ExchangeSettings::getUseTestnet)
                .orElse(true); // Если настроек нет, считаем что Testnet по умолчанию
    }

    @Transactional
    public boolean toggleTestnetMode(Long userId, String exchange) {
        ExchangeSettings settings = exchangeSettingsRepository.findByUserIdAndExchange(userId, exchange)
                .orElseGet(() -> {
                    ExchangeSettings newSettings = new ExchangeSettings();
                    newSettings.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Пользователь не найден.")));
                    newSettings.setExchange(exchange);
                    newSettings.setUseTestnet(true);
                    newSettings.setIsActive(true);
                    return exchangeSettingsRepository.save(newSettings);
                });

        settings.setUseTestnet(!settings.getUseTestnet());
        exchangeSettingsRepository.save(settings);

        return settings.getUseTestnet(); // возвращаем новое состояние
    }
}
