// src/main/java/com/chicu/neurotradebot/exchange/binance/BinanceClientProvider.java
package com.chicu.neurotradebot.exchange.binance;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceClientProvider {

    private final UserRepository userRepository;
    private final AiTradeSettingsService settingsService;
    private final ApiCredentialsService apiCredentialsService;
    private final BinanceClientFactory binanceClientFactory;

    /**
     * Возвращает BinanceApiClient, сконфигурированный ключами и режимом (spot/testnet)
     * для указанного userId.
     */
    public BinanceApiClient getClientForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        AiTradeSettings settings = settingsService.getOrCreate(user);

        ApiCredentials creds = apiCredentialsService.getSelectedCredential(
                user,
                settings.getExchange(),
                settings.isTestMode()
        );

        return binanceClientFactory.create(
                creds.getApiKey(),
                creds.getApiSecret(),
                settings.isTestMode()
        );
    }
}
