package com.chicu.neurotradebot.bot;

import com.chicu.neurotradebot.controller.TelegramUpdateHandler;
import com.chicu.neurotradebot.session.UserSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
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
                System.out.println("⛔ Пытаемся редактировать сообщение: " + editMessageText.getMessageId());
                execute(editMessageText);
            } else if (action instanceof DeleteMessage deleteMessage) {
                execute(deleteMessage);
            } else if (action instanceof SendPhoto sendPhoto) {
                Message sent = execute(sendPhoto);
                long chatId = Long.parseLong(sendPhoto.getChatId());
                UserSessionManager.setLastChartMessageId(chatId, sent.getMessageId());
            }
            // Добавь здесь другие типы, если потребуется
        } catch (Exception e) {
            e.printStackTrace(); // или лог
        }
    }
}
