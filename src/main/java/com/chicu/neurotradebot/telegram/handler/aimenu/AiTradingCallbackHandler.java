// src/main/java/com/chicu/neurotradebot/telegram/handler/AiTradingCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.networksettingsmenu.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiTradingCallbackHandler implements CallbackHandler {

    private static final String KEY = "ai_trading";
    private final NetworkSettingsViewBuilder networkView;
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

        try {
            // 1) удаляем главное меню
            sender.execute(DeleteMessage.builder()
                    .chatId(chat.toString())
                    .messageId(msgId)
                    .build());

            // 2) отправляем сетевые настройки
            sender.sendMessage(
                    chat,
                    networkView.title(),
                    networkView.markup(chat)   // <-- только chatId
            );
        } finally {
            BotContext.clear();
        }
    }
}
