// src/main/java/com/chicu/neurotradebot/telegram/handler/StrategySelectCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StrategySelectCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final AiTradeMenuBuilder aiMenu;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && u.getCallbackQuery().getData().startsWith("strat_");
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq    = u.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();
        String data = cq.getData(); // "strat_RSI_MACD" и т.п.

        BotContext.setChatId(chat);
        try {
            User user = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);

            // Сохраняем стратегию
            StrategyType type = StrategyType.valueOf(data.substring(6));
            settings.setStrategy(type);
            settingsService.save(settings);

            // Возвращаем главное меню AI
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(aiMenu.title())
                .replyMarkup(aiMenu.markup(chat))
                .build();
            sender.execute(edit);

        } finally {
            BotContext.clear();
        }
    }
}
