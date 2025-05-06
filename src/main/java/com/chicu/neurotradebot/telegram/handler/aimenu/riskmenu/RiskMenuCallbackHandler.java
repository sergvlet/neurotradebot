// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/risk/RiskMenuCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.riskmenu;

import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.riskmenu.RiskMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RiskMenuCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final RiskMenuBuilder riskBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && "ai_risk".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq    = u.getCallbackQuery();
        long chat = cq.getMessage().getChatId();
        int msgId = cq.getMessage().getMessageId();

        sender.execute(new AnswerCallbackQuery(cq.getId())); // без текста
        BotContext.setChatId(chat);

        sender.execute(EditMessageText.builder()
                .chatId(Long.toString(chat))
                .messageId(msgId)
                .text(riskBuilder.title())
                .replyMarkup(riskBuilder.markup(chat))
                .build()
        );

        BotContext.clear();
    }
}
