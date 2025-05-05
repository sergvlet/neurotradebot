// src/main/java/com/chicu/neurotradebot/telegram/handler/StartCommandHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCommandHandler implements MessageHandler {

    private final UserService userService;
    private final StartMenuBuilder builder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
            && update.getMessage().hasText()
            && "/start".equals(update.getMessage().getText().trim());
    }

    @Override
    public void handle(Update update) throws Exception {
        Long chatId = update.getMessage().getChatId();
        BotContext.setChatId(chatId);
        try {
            // Создаём/обновляем юзера
            userService.getOrCreate(chatId, update.getMessage().getFrom());
            // Рисуем меню
            sender.sendMessage(chatId, builder.title(), builder.markup(chatId));
            log.info("▶️ Обработан /start для chatId={}", chatId);
        } finally {
            BotContext.clear();
        }
    }
}
