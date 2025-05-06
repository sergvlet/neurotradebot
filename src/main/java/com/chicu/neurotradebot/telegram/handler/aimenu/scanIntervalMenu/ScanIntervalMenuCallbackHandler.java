// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/scanIntervalMenu/ScanIntervalMenuCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.scanIntervalMenu;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.scanIntervalMenu.ScanIntervalMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ScanIntervalMenuCallbackHandler implements CallbackHandler {

    private final TelegramSender sender;
    private final ScanIntervalMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
            && "ai_scan_interval".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq    = u.getCallbackQuery();
        long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();

        // Подтверждаем callback (без уведомления текста)
        sender.execute(new AnswerCallbackQuery(cq.getId()));

        // Редактируем текущее сообщение, показываем меню выбора интервала
        BotContext.setChatId(chat);
        sender.execute(EditMessageText.builder()
            .chatId(Long.toString(chat))
            .messageId(msg)
            .text(menuBuilder.title())
            .replyMarkup(menuBuilder.markup(chat))
            .build()
        );
        BotContext.clear();
    }
}
