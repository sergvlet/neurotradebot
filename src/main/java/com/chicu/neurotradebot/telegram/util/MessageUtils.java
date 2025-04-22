package com.chicu.neurotradebot.telegram.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageUtils {

    private static class MessageState {
        String text;
        InlineKeyboardMarkup markup;

        public MessageState(String text, InlineKeyboardMarkup markup) {
            this.text = text;
            this.markup = markup;
        }
    }

    private final Map<String, MessageState> lastStates = new HashMap<>();

    public void editMessage(Long chatId, Integer messageId, String text,
                            InlineKeyboardMarkup keyboard, AbsSender sender) {
        try {
            if (messageId == null) {
                log.info("📤 Отправка нового сообщения (messageId == null)");
                sendMessage(chatId, text, keyboard, sender);
                return;
            }

            String key = chatId + ":" + messageId;
            MessageState last = lastStates.get(key);

            if (last != null &&
                    Objects.equals(last.text, text) &&
                    Objects.equals(last.markup, keyboard)) {
                log.info("⏭ Пропуск редактирования — текст и клавиатура не изменились (chatId={}, messageId={})", chatId, messageId);
                return;
            }

            EditMessageText editMessage = EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();

            sender.execute(editMessage);
            lastStates.put(key, new MessageState(text, keyboard));

        } catch (TelegramApiException e) {
            log.error("❌ Ошибка при отправке/редактировании сообщения", e);
        }
    }

    public void sendMessage(Long chatId, String text, AbsSender sender) {
        try {
            int maxLength = 4096;
            if (text.length() <= maxLength) {
                SendMessage message = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(text)
                        .build();
                sender.execute(message);
            } else {
                // Разбиваем длинное сообщение на части
                for (int i = 0; i < text.length(); i += maxLength) {
                    int end = Math.min(i + maxLength, text.length());
                    String part = text.substring(i, end);
                    SendMessage message = SendMessage.builder()
                            .chatId(chatId.toString())
                            .text(part)
                            .build();
                    sender.execute(message);
                }
            }
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка при отправке сообщения", e);
        }
    }


    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard, AbsSender sender) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка при отправке сообщения с клавиатурой", e);
        }
    }
}
