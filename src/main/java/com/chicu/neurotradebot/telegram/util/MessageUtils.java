package com.chicu.neurotradebot.telegram.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class MessageUtils {

    public void editMessage(Long chatId, Integer messageId, String text,
                            InlineKeyboardMarkup keyboard, AbsSender sender) {
        try {
            if (messageId != null) {
                EditMessageText editMessage = EditMessageText.builder()
                        .chatId(chatId.toString())
                        .messageId(messageId)
                        .text(text)
                        .replyMarkup(keyboard)
                        .build();

                sender.execute(editMessage);
            } else {
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(text)
                        .replyMarkup(keyboard)
                        .build();

                Message sent = sender.execute(sendMessage);
                System.out.println("📤 Отправлено новое сообщение для chatId=" + chatId + " messageId=" + sent.getMessageId());
            }
        } catch (TelegramApiException e) {
            if (e.getMessage().contains("message is not modified")) {
                System.out.println("⚠️ Попытка редактирования без изменений, Telegram отказал");
            } else {
                System.err.println("❌ Ошибка при отправке/редактировании сообщения");
                e.printStackTrace();
            }
        }
    }
}
