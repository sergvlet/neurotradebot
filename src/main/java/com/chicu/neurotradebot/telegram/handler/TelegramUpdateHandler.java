package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TelegramUpdateHandler {

    private final StartMenuBuilder startMenuBuilder;

    public SendMessage handle(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if ("/start".equals(messageText)) {
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("""
                          👋 Привет!

                          Выберите действие в меню:
                          """)
                    .replyMarkup(startMenuBuilder.buildMainMenu())
                    .parseMode("Markdown")
                    .build();
        } else {
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("""
                          ⚠️ Неизвестная команда. Пожалуйста, используйте меню.
                          """)
                    .replyMarkup(startMenuBuilder.buildMainMenu())
                    .parseMode("Markdown")
                    .build();
        }
    }
}
