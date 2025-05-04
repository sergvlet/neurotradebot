// src/main/java/com/chicu/neurotradebot/telegram/handler/CommandMessageHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandMessageHandler implements MessageHandler {

    private final List<MenuDefinition> menus;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        BotContext.setChatId(chatId);

        menus.stream()
            .filter(menu -> menu.keys().contains(text))
            .findFirst()
            .ifPresentOrElse(menu -> {
                try {
                    sender.execute(SendMessage.builder()
                            .chatId(chatId.toString())
                            .text(menu.title())
                            .replyMarkup(menu.markup(chatId))
                            .build());

                    log.info("📥 Команда {} вызвала меню {}", text, menu.getClass().getSimpleName());
                } catch (Exception e) {
                    log.error("Ошибка при вызове меню {}", menu.getClass().getSimpleName(), e);
                }
            }, () -> log.warn("⚠️ Нет меню для команды: {}", text));
    }
}
