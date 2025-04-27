package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.AccessControlService;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import com.chicu.neurotradebot.user.service.ExchangeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class SettingsCallbackHandler {

    private final StartMenuBuilder menuBuilder;
    private final AccessControlService accessControlService;
    private final ExchangeSettingsService exchangeSettingsService;

    public EditMessageText handle(long chatId, Integer messageId) {
        if (!accessControlService.hasActiveSubscription(chatId)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("⛔ У вас нет активной подписки.\n\nПожалуйста, нажмите 👤 Подписка и выберите подходящий тариф.")
                    .build();
        }

        boolean useTestnet = exchangeSettingsService.isTestnetEnabled(chatId, "BINANCE");
        String network = useTestnet ? "Testnet (тестовая сеть)" : "Real (реальная сеть)";

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("⚙️ Настройки торговли:\n\nТекущая сеть: " + network + "\n\nВыберите нужный параметр:\n🔹 Переключить режим торговли (Тест/Реал)\n🔹 Настроить API-ключи\n🔹 Выбрать биржу")
                .replyMarkup(menuBuilder.buildSettingsMenu())
                .build();
    }

}
