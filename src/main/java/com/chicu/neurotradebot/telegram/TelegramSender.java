// src/main/java/com/chicu/neurotradebot/telegram/TelegramSender.java
package com.chicu.neurotradebot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSender {
    private final ApplicationContext ctx;

    private <T extends Serializable> T executeInternal(BotApiMethod<T> method) throws TelegramApiException {
        return ctx.getBean(NeuroTradeBot.class).execute(method);
    }

    /**
     * Выполнить произвольный метод Telegram API, пробрасывая исключения.
     */
    public <T extends Serializable> T execute(BotApiMethod<T> method) throws TelegramApiException {
        try {
            return executeInternal(method);
        } catch (TelegramApiException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("message is not modified")) {
                log.debug("Игнорирую попытку отредактировать сообщение без изменений");
                return null;
            }
            throw e;
        }
    }

    /**
     * Тихо выполнить метод (без логирования ошибок).
     */
    public <T extends Serializable> void executeSilently(BotApiMethod<T> method) {
        try {
            executeInternal(method);
        } catch (Exception e) {
            log.warn("⚠️ Ошибка при silent-отправке: {}", e.getMessage());
        }
    }

    /**
     * Отправить простое текстовое сообщение.
     */
    public void sendMessage(Long chatId, String text) {
        executeSilently(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build());
    }

    /**
     * Отправить сообщение с inline-клавиатурой.
     */
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        executeSilently(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(markup)
                .build());
    }

    /**
     * Редактирует текст и клавиатуру существующего сообщения.
     */
    public void editMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) {
        executeSilently(EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .replyMarkup(markup)
                .build());
    }

    /**
     * Удаляет сообщение (если messageId != null).
     */
    public void deleteMessage(Long chatId, Integer messageId) {
        if (messageId == null) return;
        executeSilently(DeleteMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build());
    }
}
