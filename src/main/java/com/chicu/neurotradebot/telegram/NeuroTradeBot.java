// src/main/java/com/chicu/neurotradebot/telegram/NeuroTradeBot.java
package com.chicu.neurotradebot.telegram;

import com.chicu.neurotradebot.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class NeuroTradeBot extends TelegramLongPollingBot {

    private final TelegramProperties props;
    private final MessageDispatcher  messageDispatcher;
    private final UpdateDispatcher   callbackDispatcher;

    @Override
    public String getBotUsername() {
        return props.getUsername();
    }

    @Override
    public String getBotToken() {
        return props.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            callbackDispatcher.dispatch(update); // <-- исправлено
        } else {
            messageDispatcher.dispatch(update);
        }
    }
}
