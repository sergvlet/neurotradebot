package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.AccessControlService;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import com.chicu.neurotradebot.user.service.ExchangeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class ToggleTradingModeHandler {

    private final StartMenuBuilder startMenuBuilder;
    private final AccessControlService accessControlService;
    private final ExchangeSettingsService exchangeSettingsService;

    public EditMessageText handle(long chatId, Integer messageId) {
        if (!accessControlService.hasActiveSubscription(chatId)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                      ⛔ У вас нет активной подписки.

                      Пожалуйста, нажмите 👤 *Подписка* и выберите подходящий тариф.
                      """)
                    .parseMode("Markdown")
                    .build();
        }

        // ✅ Сначала просто переключаем режим
        exchangeSettingsService.toggleTestnetMode(chatId, "BINANCE");

        // ✅ Потом узнаем какой сейчас режим
        boolean nowTestnet = exchangeSettingsService.isTestnetEnabled(chatId, "BINANCE");

        String network = nowTestnet ? "Testnet (тестовая сеть)" : "Real (реальная сеть)";

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                  🎯 Режим торговли изменён!

                  Теперь используется: *%s*
                  """.formatted(network)) // ✅ используем .formatted(network) вместо конкатенации
                .replyMarkup(startMenuBuilder.buildSettingsMenu())
                .parseMode("Markdown")
                .build();
    }
}
