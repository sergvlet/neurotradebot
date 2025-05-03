// src/main/java/com/chicu/neurotradebot/telegram/handler/MenuMessageHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuMessageHandler implements MessageHandler {

    private final List<MenuDefinition> menus;
    private final TelegramSender       sender;

    @Override
    public boolean canHandle(Update update) {
        // это ли текстовое сообщение с содержимым, совпадающим с одной из ваших команд
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        String text = update.getMessage().getText().trim();
        return menus.stream().anyMatch(m -> m.keys().contains(text));
    }

    @Override
    public void handle(Update update) throws Exception {
        var msg    = update.getMessage();
        String chatId = msg.getChatId().toString();
        String text   = msg.getText().trim();

        // находим определения меню по ключам
        for (MenuDefinition m : menus) {
            if (m.keys().contains(text)) {
                // отправляем новое сообщение с меню (или редактируем, если нужно)
                sender.execute(
                    SendMessage.builder()
                        .chatId(chatId)
                        .text(m.title())
                        .replyMarkup(m.markup(msg.getChatId()))
                        .build()
                );
                log.info("Отправлено меню '{}' по команде '{}' в чат {}", m.title(), text, chatId);
                return;
            }
        }

        // теоретически сюда не дойдёт — canHandle гарантирует, что кто-то найдётся
        log.warn("Команда '{}' не сопоставилась ни с одним меню", text);
    }
}
