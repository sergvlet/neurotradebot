// src/main/java/com/chicu/neurotradebot/telegram/UpdateDispatcher.java
package com.chicu.neurotradebot.telegram;

import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDispatcher {

    private final List<CallbackHandler> handlers;

    public void dispatch(Update update) {
        if (!update.hasCallbackQuery()) {
            return;
        }

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        // Получаем объект Telegram-пользователя
        org.telegram.telegrambots.meta.api.objects.User tgUser = update
                .getCallbackQuery()
                .getFrom();

        // Устанавливаем оба значения в BotContext
        BotContext.setContext(chatId, tgUser);

        String data = update.getCallbackQuery().getData();
        log.debug("Получен CallbackQuery: {}", data);

        for (CallbackHandler handler : handlers) {
            if (handler.canHandle(update)) {
                try {
                    handler.handle(update);
                } catch (Exception e) {
                    log.error("Ошибка в обработчике для '{}'", data, e);
                } finally {
                    BotContext.clear();
                }
                return;
            }
        }

        log.warn("Нет обработчика для callbackData='{}'", data);
        BotContext.clear();
    }
}
