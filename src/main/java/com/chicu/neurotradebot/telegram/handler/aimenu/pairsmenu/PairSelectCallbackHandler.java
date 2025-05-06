// src/main/java/com/chicu/neurotradebot/telegram/handler/PairSelectCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PairSelectCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;
    private final AiTradeMenuBuilder aiBuilder;  // ваш билдeр главного AI-меню

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
            && u.getCallbackQuery().getData().startsWith("pair_select_");
    }

    @Override
    @Transactional
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        Long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();
        String data   = cq.getData();               // "pair_select_BTCUSDT"
        String symbol = data.substring("pair_select_".length());

        // Подтверждаем callback и контекст
        sender.execute(new AnswerCallbackQuery(cq.getId()));
        BotContext.setChatId(chat);

        try {
            var user     = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);

            // Сохраняем выбранную пару (заменяем на новый mutable список)
            settings.setPairs(new ArrayList<>(List.of(symbol)));
            settings.setApiSetupStep(ApiSetupStep.NONE);
            settingsService.save(settings);

            // Формируем текст и сразу меняем на него и кнопки на главное AI-меню
            String base  = symbol.substring(0, symbol.length() - 4);
            String quote = symbol.substring(symbol.length() - 4);

            // 1) Меняем текст
            sender.execute(EditMessageText.builder()
                    .chatId(chat.toString())
                    .messageId(msgId)
                    .text("✅ Выбрана пара: " + base + "/" + quote)
                    .build()
            );

            // 2) Меняем кнопки на главное AI-меню
            sender.execute(EditMessageReplyMarkup.builder()
                    .chatId(chat.toString())
                    .messageId(msgId)
                    .replyMarkup(aiBuilder.markup(chat))
                    .build()
            );
        } finally {
            BotContext.clear();
        }
    }
}
