// src/main/java/com/chicu/neurotradebot/telegram/handler/ExchangeSelectionCallbackHandler.java
package com.chicu.neurotradebot.handler;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.view.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeSelectionCallbackHandler implements CallbackHandler {

    private static final String PREFIX = "exchange:";
    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final NetworkSettingsViewBuilder netBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
            && update.getCallbackQuery().getData().startsWith(PREFIX);
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq    = update.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        Integer msgId = cq.getMessage().getMessageId();

        BotContext.setChatId(chat);
        try {
            String market = cq.getData().substring(PREFIX.length()); // binance или ftx
            User user = userService.getOrCreate(chat);
            AiTradeSettings s = settingsService.getOrCreate(user);
            s.setExchange(market);
            settingsService.save(s);

            log.info("🌐 Биржа '{}' сохранена для user={}", market, chat);

            // убрать спиннер
            sender.execute(new AnswerCallbackQuery(cq.getId()));

            // перерисовать меню сетевых настроек
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msgId)
                .text(netBuilder.title())
                // fromAi = true, если открывали из AI-меню, иначе false; 
                // здесь подставьте вашу логику или храните флаг в AiTradeSettings
                .replyMarkup(netBuilder.markup(chat, /*fromAi=*/ true))
                .build();
            sender.execute(edit);

        } finally {
            BotContext.clear();
        }
    }
}
