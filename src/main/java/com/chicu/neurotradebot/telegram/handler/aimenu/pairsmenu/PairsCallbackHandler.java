// src/main/java/com/chicu/neurotradebot/telegram/handler/PairsCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.networksettingsmenu.PairsAddMethodMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PairsCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final PairsAddMethodMenuBuilder menuBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && "ai_pairs".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq   = u.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();

        sender.execute(new AnswerCallbackQuery(cq.getId()));
        

        try {
            // Сбрасываем шаг, чтобы не показывать меню снова
            var user     = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);
            settings.setApiSetupStep(ApiSetupStep.NONE);
            settingsService.save(settings);

            // Редактируем текущее сообщение вместо нового, убирая спам
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(menuBuilder.title())
                .replyMarkup(menuBuilder.markup(chat))
                .build();

            sender.execute(edit);
        } finally {
            BotContext.clear();
        }
    }
}
