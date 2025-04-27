package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
public class SubscriptionCallbackHandler {

    private final StartMenuBuilder menuBuilder;

    public SubscriptionCallbackHandler(StartMenuBuilder menuBuilder) {
        this.menuBuilder = menuBuilder;
    }

    public EditMessageText handle(long chatId, Integer messageId) {
        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("👤 Раздел подписки. Здесь будет управление и оплата.")
                .replyMarkup(menuBuilder.buildMainMenu())
                .build();
    }
}
