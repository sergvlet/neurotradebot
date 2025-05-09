// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/ApplyNetworkSettingsAiCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu;

import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ApplyNetworkSettingsAiCallbackHandler implements CallbackHandler {

    private static final String KEY = "apply_network_settings_ai";

    private final TelegramSender sender;
    private final AiTradeMenuBuilder aiMenu;

    @Override
    public boolean canHandle(Update upd) {
        return upd.hasCallbackQuery()
            && KEY.equals(upd.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update upd) throws Exception {
        var cq    = upd.getCallbackQuery();
        String cqId = cq.getId();
        Long chat  = cq.getMessage().getChatId();
        Integer msgId = cq.getMessage().getMessageId();

        // 1) Убираем «крутилку»
        sender.execute(new AnswerCallbackQuery(cqId));

        // 2) Рисуем AI-меню заново
        sender.execute(EditMessageText.builder()
            .chatId(chat.toString())
            .messageId(msgId)
            .text(aiMenu.title())
            .replyMarkup(aiMenu.markup(chat))
            .build()
        );
    }
}
