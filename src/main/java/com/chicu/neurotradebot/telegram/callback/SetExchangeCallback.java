package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.service.UserApiKeysService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class SetExchangeCallback implements CallbackProcessor {

    private final UserSettingsService settingsService;
    private final UserApiKeysService apiKeysService;
    private final MessageUtils messageUtils;
    private final ExchangeMenuCallback exchangeMenuCallback;

    @Override
    public BotCallback callback() {
        return BotCallback.SET_EXCHANGE;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        String[] parts = callbackData.split(":");
        if (parts.length < 2) {
            messageUtils.editMessage(chatId, messageId, "❌ Ошибка: биржа не указана", null, sender);
            return;
        }

        Exchange selected = Exchange.valueOf(parts[1]);
        var settings = settingsService.getOrCreate(chatId);
        settings.setExchange(selected);
        settingsService.setExchange(chatId, selected);

        boolean isDemo = settings.getTradeMode() == null || settings.getTradeMode().name().equals("DEMO");

        boolean hasKeys = isDemo
                ? apiKeysService.hasTestKeys(chatId, selected)
                : apiKeysService.hasRealKeys(chatId, selected);

        if (hasKeys) {
            // ✅ Ключи уже есть — сразу переходим в меню выбора символа
            exchangeMenuCallback.process(chatId, messageId, BotCallback.EXCHANGE_MENU.getValue(), sender);
        } else {
            // 🔐 Ключей нет — просим ввести API Key
            messageUtils.editMessage(chatId, messageId,
                    "🔐 Введите API Key для биржи " + selected + (isDemo ? " (тестовый режим):" : " (реальный режим):"),
                    null, sender);

            settingsService.setWaitingForInput(chatId,
                    (isDemo ? "API_KEY_TEST" : "API_KEY_REAL") + ":" + selected.name());
        }
    }
}
