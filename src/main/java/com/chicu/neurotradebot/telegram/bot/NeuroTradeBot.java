package com.chicu.neurotradebot.telegram.bot;

import com.chicu.neurotradebot.telegram.handler.TelegramUpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

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
        Object result = telegramUpdateHandler.handle(update);

        if (result instanceof List<?> list) {
            for (Object action : list) {
                executeSafely(action);
            }
        } else {
            executeSafely(result);
        }
    }

    private void executeSafely(Object action) {
        try {
            if (action instanceof SendMessage sendMessage) {
                execute(sendMessage);
            } else if (action instanceof EditMessageText editMessageText) {
                execute(editMessageText);
            } else if (action instanceof DeleteMessage deleteMessage) {
                execute(deleteMessage);
            }
            // можно добавить и другие типы, если используешь
        } catch (Exception e) {
            e.printStackTrace(); // или лог
        }
    }
}
