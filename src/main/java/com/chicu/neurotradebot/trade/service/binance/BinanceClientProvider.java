// src/main/java/com/chicu/neurotradebot/trade/service/binance/BinanceClientProvider.java
package com.chicu.neurotradebot.trade.service.binance;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceClientProvider {

    private final AiTradeSettingsService settingsService;
    private final BinanceClientFactory   clientFactory;

    /**
     * Возвращает BinanceApiClient для данного chatId,
     * используя первые сохранённые ApiCredentials из AiTradeSettings.
     */
    public BinanceApiClient getClientForUser(Long chatId) {
        AiTradeSettings cfg = settingsService.getByChatId(chatId);

        ApiCredentials creds = cfg.getCredentials().stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "API-ключи не настроены для Binance (chatId=" + chatId + ")"
                        )
                );

        return clientFactory.create(
                creds.getApiKey(),
                creds.getApiSecret(),
                cfg.isTestMode()
        );
    }
}
