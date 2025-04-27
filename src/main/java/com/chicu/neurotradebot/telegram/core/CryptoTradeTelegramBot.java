package com.chicu.neurotradebot.telegram.core;

import com.chicu.neurotradebot.telegram.config.TelegramBotConfigProperties;
import com.chicu.neurotradebot.telegram.handler.TelegramUpdateHandler;
import com.chicu.neurotradebot.telegram.handler.TelegramCallbackDispatcher;
import com.chicu.neurotradebot.telegram.handler.callback.ApiKeySetupHandler;
import com.chicu.neurotradebot.user.service.ApiKeySetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CryptoTradeTelegramBot extends TelegramLongPollingBot {

    private final TelegramBotConfigProperties config;
    private final TelegramUpdateHandler updateHandler;
    private final TelegramCallbackDispatcher callbackDispatcher;
    private final ApiKeySetupService apiKeySetupService;
    private final ApiKeySetupHandler apiKeySetupHandler;

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                long chatId = update.getMessage().getChatId();

                if (apiKeySetupService.isInSetup(chatId)) {
                    // если пользователь в процессе настройки API-ключей
                    SendMessage message = apiKeySetupHandler.handleApiKeyInput(update);
                    execute(message);
                } else {
                    // обычная логика обработки текста
                    SendMessage message = updateHandler.handle(update);
                    execute(message);
                }
            } else if (update.hasCallbackQuery()) {
                BotApiMethod<?> response = callbackDispatcher.handle(update);
                execute(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
