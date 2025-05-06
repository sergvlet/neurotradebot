// src/main/java/com/chicu/neurotradebot/telegram/handler/TradeModeCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.enums.TradeMode;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TradeModeCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final AiTradeMenuBuilder aiMenu;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        String data = update.getCallbackQuery().getData();
        return data != null && data.startsWith("mode_");
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq     = update.getCallbackQuery();
        Long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();
        String data = cq.getData();  // e.g. "mode_SPOT"

        BotContext.setChatId(chat);
        try {
            // 1) Получаем пользователя и его настройки
            User user = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);

            // 2) Сохраняем выбранный режим торговли
            TradeMode mode = TradeMode.valueOf(data.substring(5));
            settings.setTradeMode(mode);
            settingsService.save(settings);

            // 3) Возвращаемся в главное меню AI-режима
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msgId)
                .text(aiMenu.title())
                .replyMarkup(aiMenu.markup(chat))
                .build();
            sender.execute(edit);
        } finally {
            BotContext.clear();
        }
    }
}
