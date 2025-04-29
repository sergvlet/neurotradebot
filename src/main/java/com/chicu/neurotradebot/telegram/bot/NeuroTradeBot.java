package com.chicu.neurotradebot.telegram.bot;

import com.chicu.neurotradebot.telegram.handler.TelegramUpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class NeuroTradeBot extends TelegramLongPollingBot {

    private final TelegramUpdateHandler telegramUpdateHandler;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Object response = telegramUpdateHandler.handle(update);

        if (response instanceof BotApiMethod<?> method) {
            try {
                execute(method);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (response instanceof SendMessage sendMessage) {
            try {
                execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
