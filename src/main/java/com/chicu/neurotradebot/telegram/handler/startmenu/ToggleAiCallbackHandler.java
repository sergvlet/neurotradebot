// src/main/java/com/chicu/neurotradebot/telegram/handler/ToggleAiCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.startmenu;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToggleAiCallbackHandler implements CallbackHandler {

    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
            && "toggle_ai".equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq = update.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        BotContext.setChatId(chatId);
        try {
            // Ваша логика переключения AI
            boolean newState = true; // пример: переключили в состояние ON

            // Формируем и отправляем ответ на callbackQuery
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(cq.getId());
            answer.setText("AI is now " + (newState ? "ON" : "OFF"));
            sender.execute(answer);

            log.info("AI toggled to {}", newState);
        } finally {
            BotContext.clear();
        }
    }
}
