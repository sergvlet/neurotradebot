// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/ScalpingConfigInvokeHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.ScalpingConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ScalpingConfigInvokeHandler implements CallbackHandler {

    private final ScalpingConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return "config_strat_SCALPING".equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq  = update.getCallbackQuery();
        Long chatId       = cq.getMessage().getChatId();
        Integer messageId = cq.getMessage().getMessageId();
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
