package com.chicu.neurotradebot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSender {
    private final ApplicationContext ctx;

    public <T extends Serializable> T execute(BotApiMethod<T> method) throws TelegramApiException {
        try {
            NeuroTradeBot bot = ctx.getBean(NeuroTradeBot.class);
            return bot.execute(method);
        } catch (TelegramApiRequestException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("message is not modified")) {
                log.debug("Игнорирую попытку отредактировать сообщение без изменений");
                return null;
            }
            throw e;
        }
    }

    public void sendMessage(Long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка при отправке сообщения: {}", e.getMessage(), e);
        }
    }

    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .replyMarkup(markup)
                    .build();
            execute(message);
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка при отправке сообщения с клавиатурой: {}", e.getMessage(), e);
        }
    }

    // ✅ Добавляем этот метод:
    public <T extends Serializable> void executeSilently(BotApiMethod<T> method) {
        try {
            execute(method);
        } catch (Exception e) {
            log.warn("⚠️ Ошибка при silent-отправке: {}", e.getMessage());
        }
    }
}
