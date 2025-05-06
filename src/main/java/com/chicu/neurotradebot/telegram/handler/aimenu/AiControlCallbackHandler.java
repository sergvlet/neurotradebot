// src/main/java/com/chicu/neurotradebot/telegram/handler/AiControlCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.networksettingsmenu.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiControlCallbackHandler implements CallbackHandler {

    private static final String KEY = "ai_control";

    private final TelegramSender sender;
    private final NetworkSettingsViewBuilder networkBuilder;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
            && KEY.equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq = update.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        Integer msgId = cq.getMessage().getMessageId();
        BotContext.setChatId(chatId);
        try {
            // 1) Answer spinner
            sender.execute(new AnswerCallbackQuery(cq.getId()));

            // 2) Показываем сетевые настройки, fromAi = true
            String text = networkBuilder.title();
            var markup = networkBuilder.markup(chatId, true);

            // Редактируем текущее сообщение, если это callback из Inline меню
            EditMessageText edit = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(msgId)
                .text(text)
                .replyMarkup(markup)
                .build();
            sender.execute(edit);

            log.info("Перешли в сетевые настройки из AI-меню, chatId={}", chatId);
        } finally {
            BotContext.clear();
        }
    }
}
