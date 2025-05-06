// src/main/java/com/chicu/neurotradebot/telegram/handler/AiStrategyCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.StrategyMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AiStrategyCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final StrategyMenuBuilder strategyMenu;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && "ai_strategy".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq    = u.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();

        BotContext.setChatId(chat);
        try {
            // Инициализируем настройки, если нужно
            var user     = userService.getOrCreate(chat);
            settingsService.getOrCreate(user);

            // Показываем меню стратегий
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(strategyMenu.title())
                .replyMarkup(strategyMenu.markup(chat))
                .build();
            sender.execute(edit);
        } finally {
            BotContext.clear();
        }
    }
}
