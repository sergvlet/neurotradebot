package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.model.Exchange;
import com.chicu.neurotradebot.trade.service.UserApiKeysService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class ApiKeyInputProcessor {

    private final UserSettingsService settingsService;
    private final UserApiKeysService apiKeysService;
    private final MessageUtils messageUtils;
    private final ExchangeMenuCallback exchangeMenuCallback;

    public void process(Message message, AbsSender sender) {
        Long chatId = message.getChatId();
        String text = message.getText();

        String waiting = settingsService.getWaitingFor(chatId);
        if (waiting == null || !waiting.startsWith("API_KEY_")) return;

        String[] parts = waiting.split(":");
        if (parts.length < 2) return;

        String mode = parts[0];  // API_KEY_TEST или API_KEY_REAL или API_KEY_TEST_SECRET и т.п.
        Exchange exchange = Exchange.valueOf(parts[1]);

        if (!mode.endsWith("_SECRET")) {
            // 🔐 Получили API Key — теперь ждём Secret Key
            settingsService.setTemp(chatId, "API_KEY", text);
            settingsService.setWaitingForInput(chatId, mode + "_SECRET:" + exchange.name());

            messageUtils.sendMessage(chatId,
                    "🛡 Теперь введите Secret Key для " + exchange +
                            (mode.contains("TEST") ? " (тестовый режим):" : " (реальный режим):"),
                    sender);
        } else {
            // ✅ Получили Secret Key — сохраняем оба
            String apiKey = settingsService.getTemp(chatId, "API_KEY");

            if (mode.startsWith("API_KEY_TEST")) {
                apiKeysService.saveTestKeys(chatId, exchange, apiKey, text);
            } else {
                apiKeysService.saveRealKeys(chatId, exchange, apiKey, text);
            }

            settingsService.clearWaiting(chatId);
            settingsService.clearTemp(chatId, "API_KEY");

            messageUtils.sendMessage(chatId,
                    "✅ Ключи для " + exchange + " успешно сохранены. Возвращаюсь к выбору биржи.",
                    sender);

            // Переход обратно в меню выбора биржи
            exchangeMenuCallback.process(chatId, null, BotCallback.EXCHANGE_MENU.getValue(), sender);
        }
    }
}
