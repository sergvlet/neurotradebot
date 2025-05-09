// src/main/java/com/chicu/neurotradebot/telegram/navigation/NavigationBackCallbackHandler.java
package com.chicu.neurotradebot.telegram.navigation;

import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class NavigationBackCallbackHandler implements CallbackHandler {

    private final NavigationHistoryService navHistory;
    private final MenuDefinitionRegistry   menuRegistry;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update upd) {
        return upd.hasCallbackQuery()
            && "back".equals(upd.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update upd) {
        long chatId = upd.getCallbackQuery().getMessage().getChatId();
        int  msgId  = upd.getCallbackQuery().getMessage().getMessageId();

        String prevKey = navHistory.popPrevious(chatId);
        if (prevKey == null) {
            sender.deleteMessage(chatId, msgId);
            return;
        }

        var menu = menuRegistry.get(prevKey);
        sender.editMessage(
            chatId,
            msgId,
            menu.title(),
            menu.markup(chatId)
        );
    }
}
