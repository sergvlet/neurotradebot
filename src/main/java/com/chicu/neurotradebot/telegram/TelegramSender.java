package com.chicu.neurotradebot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSender {
    private final ApplicationContext ctx;

    /**
     * Исполняет метод Telegram API, возвращает результат или выбрасывает исключение.
     */
    private <T extends Serializable> T executeInternal(BotApiMethod<T> method) throws TelegramApiException {
        return ctx.getBean(NeuroTradeBot.class).execute(method);
    }

    /**
     * Исполняет метод Telegram API, пробрасывая TelegramApiException,
     * но игнорирует ошибку "message is not modified".
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
     * Тихо выполнить метод, не логируя “message is not modified”,
     * но предупреждая об остальных ошибках.
     */
    public <T extends Serializable> void executeSilently(BotApiMethod<T> method) {
        try {
            executeInternal(method);
        } catch (TelegramApiException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("message is not modified")) {
                // полностью игнорируем
                return;
            }
            log.warn("⚠️ Ошибка при silent-отправке: {}", msg);
        } catch (Exception e) {
            log.warn("⚠️ Неожиданная ошибка при silent-отправке:", e);
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
     * Отредактировать текст и клавиатуру существующего сообщения.
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
     * Удалить сообщение (если messageId != null).
     */
    public void deleteMessage(Long chatId, Integer messageId) {
        if (messageId == null) return;
        executeSilently(DeleteMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build());
    }
}
