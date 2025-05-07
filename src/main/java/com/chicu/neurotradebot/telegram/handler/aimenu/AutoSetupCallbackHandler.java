// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/AutoSetupCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu;

import com.chicu.neurotradebot.service.AutoSetupService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AutoSetupCallbackHandler implements CallbackHandler {

    private final AutoSetupService    autoSetup;
    private final TelegramSender      sender;
    private final AiTradeMenuBuilder  menuBuilder;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
                && "ai_autoconfig".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq      = u.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        Integer msg = cq.getMessage().getMessageId();

        // Запуск автонастройки для этого chatId
        String report = autoSetup.runAutoSetupForChat(chatId);

        // Собираем текст: отчёт + оригинальный заголовок
        String title = menuBuilder.title();
        var kb = menuBuilder.markup(chatId);
        String newText = report + "\n\n" + title;

        sender.execute(
                EditMessageText.builder()
                        .chatId(chatId.toString())
                        .messageId(msg)
                        .text(newText)
                        .replyMarkup(kb)
                        .build()
        );
    }
}
