// src/main/java/com/chicu/neurotradebot/telegram/handler/AiTradeModeCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.trademenu;

import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.TradeModeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик кнопки "ai_trade_mode" — открывает меню выбора режима торговли.
 */
@Component
@RequiredArgsConstructor
public class AiTradeModeCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final TradeModeMenuBuilder tradeModeMenu;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
            && "ai_trade_mode".equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq    = update.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();

        BotContext.setChatId(chat);
        try {
            // Убедимся, что настройки существуют (инициализируем если нужно)
            var user     = userService.getOrCreate(chat);
            settingsService.getOrCreate(user);

            // Рисуем меню выбора режима торговли
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(tradeModeMenu.title())
                .replyMarkup(tradeModeMenu.markup(chat))
                .build();

            sender.execute(edit);
        } finally {
            BotContext.clear();
        }
    }
}
