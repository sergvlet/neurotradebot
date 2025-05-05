// src/main/java/com/chicu/neurotradebot/telegram/handler/StartMenuCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartMenuCallbackHandler implements CallbackHandler {

    private static final String KEY = "start_menu";
    private final StartMenuBuilder startMenu;
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

        BotContext.setChatId(chat);
        try {
            // 1) удаляем старое сообщение, если хотим полностью заменить
            sender.execute(DeleteMessage.builder()
                .chatId(chat.toString())
                .messageId(msgId)
                .build());

            // 2) отправляем главное меню заново
            sender.sendMessage(
                chat,
                startMenu.title(),
                startMenu.markup(chat)
            );
        } finally {
            BotContext.clear();
        }
    }
}
