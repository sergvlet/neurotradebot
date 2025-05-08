// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/MacdConfigInvokeHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.MacdConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class MacdConfigInvokeHandler implements CallbackHandler {

    private final MacdConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return "config_strat_MACD".equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq = update.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        Integer messageId = cq.getMessage().getMessageId();  // <- берём ID того же сообщения

        // Вместо sendMessage(...), buildOrEditMenu при messageId!=null
        // вызовет sender.editMessage(...) и перерисует текущее сообщение.
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
