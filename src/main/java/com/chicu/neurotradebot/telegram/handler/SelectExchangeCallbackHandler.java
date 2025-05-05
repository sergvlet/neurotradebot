// src/main/java/com/chicu/neurotradebot/telegram/handler/SelectExchangeCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.ExchangeListMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class SelectExchangeCallbackHandler implements CallbackHandler {

    private static final String KEY = "select_exchange";
    private final ExchangeListMenuBuilder listBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
            && KEY.equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq    = update.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        Integer msgId = cq.getMessage().getMessageId();

        BotContext.setChatId(chat);
        try {
            // убрать спиннер
            sender.execute(new AnswerCallbackQuery(cq.getId()));
            // отрисовать меню списка бирж
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msgId)
                .text(listBuilder.title())
                .replyMarkup(listBuilder.markup(chat))
                .build();
            sender.execute(edit);
        } finally {
            BotContext.clear();
        }
    }
}
