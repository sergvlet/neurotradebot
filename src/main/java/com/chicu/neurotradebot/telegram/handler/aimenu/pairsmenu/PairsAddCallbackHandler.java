// src/main/java/com/chicu/neurotradebot/telegram/handler/PairsAddCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.PairsAddMethodMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Переход в подменю выбора метода добавления пар.
 */
@Component
@RequiredArgsConstructor
public class PairsAddCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final PairsAddMethodMenuBuilder methodMenu;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && "pairs_add".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq   = u.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();

        // 1) Снимаем таймер
        sender.execute(new AnswerCallbackQuery(cq.getId()));

        // 2) Выводим подменю
        BotContext.setChatId(chat);
        try {
            // Ensure settings exists
            var user = userService.getOrCreate(chat);
            settingsService.getOrCreate(user);

            var edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(methodMenu.title())
                .replyMarkup(methodMenu.markup(chat))
                .build();
            sender.execute(edit);
        } finally {
            BotContext.clear();
        }
    }
}
