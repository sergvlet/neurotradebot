// src/main/java/com/chicu/neurotradebot/telegram/handler/GenericMenuCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import com.chicu.neurotradebot.telegram.TelegramSender;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenericMenuCallbackHandler implements CallbackHandler {

    private final List<MenuDefinition> menus;
    private final TelegramSender       sender;

    @Override
    public boolean canHandle(CallbackQuery q) {
        String data = q.getData();
        return menus.stream().anyMatch(m -> m.keys().contains(data));
    }

    @Override
    public void handle(CallbackQuery q) throws Exception {
        String data   = q.getData();
        Long   chatId = q.getMessage().getChatId();
        int    msgId  = q.getMessage().getMessageId();

        // Скрываем spinner
        sender.execute(new AnswerCallbackQuery(q.getId()));

        // Ищем нужное меню и редактируем сообщение
        for (MenuDefinition m : menus) {
            if (m.keys().contains(data)) {
                sender.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(msgId)
                    .text(m.title())
                    .replyMarkup(m.markup(chatId))    // ← передаём chatId
                    .build()
                );
                log.info("Показано меню '{}' для chatId={}", m.title(), chatId);
                return;
            }
        }

        log.warn("Нет определения меню для callbackData='{}'", data);
    }
}
