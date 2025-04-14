package com.chicu.neurotradebot.telegram.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageUtils {

    public void editMessage(Long chatId, Integer messageId, String text,
                            InlineKeyboardMarkup keyboard, AbsSender sender) {
        try {
            if (messageId == null) {
                log.info("📤 Отправлено новое сообщение для chatId={}", chatId);
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(text)
                        .replyMarkup(keyboard)
                        .build();
                sender.execute(sendMessage);
                return;
            }

            EditMessageText editMessage = EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();
            sender.execute(editMessage);

        } catch (TelegramApiException e) {
            log.error("❌ Ошибка при отправке/редактировании сообщения", e);
        }
    }
    public String getLastCallbackData(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        return null;
    }

}
