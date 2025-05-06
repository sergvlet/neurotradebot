// src/main/java/com/chicu/neurotradebot/telegram/MessageDispatcher.java
package com.chicu.neurotradebot.telegram;

import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageDispatcher {

    // Разделяем обработчики на два типа
    private final List<CallbackHandler> callbackHandlers;
    private final List<MessageHandler> messageHandlers;

    public void dispatch(Update update) {
        // сначала — коллбэки
        if (update.hasCallbackQuery()) {
            String key = update.getCallbackQuery().getData();
            for (CallbackHandler handler : callbackHandlers) {
                if (handler.canHandle(update)) {
                    try {
                        handler.handle(update);
                        log.info("📥 Обработан callback '{}', вызван {}", key, handler.getClass().getSimpleName());
                    } catch (Exception e) {
                        log.error("❌ Ошибка при обработке callback {} в {}", key, handler.getClass().getSimpleName(), e);
                    }
                    return;
                }
            }
            log.warn("⚠️ Нет CallbackHandler для callback '{}'", key);
            return;
        }

        // затем — текстовые и прочие сообщения
        for (MessageHandler handler : messageHandlers) {
            if (handler.canHandle(update)) {
                try {
                    handler.handle(update);
                } catch (Exception e) {
                    log.error("❌ Ошибка в MessageHandler {}", handler.getClass().getSimpleName(), e);
                }
                return;
            }
        }

        log.warn("❓ Не найден обработчик для update: {}", update);
    }
}
