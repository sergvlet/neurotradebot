// src/main/java/com/chicu/neurotradebot/telegram/MessageDispatcher.java
package com.chicu.neurotradebot.telegram;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
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

    private final List<MessageHandler> handlers;

    public void dispatch(Update update) {
        if (update.hasCallbackQuery()) {
            String key = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            for (MessageHandler handler : handlers) {
                if (handler instanceof MenuDefinition def && def.keys().contains(key)) {
                    try {
                        def.handle(update);
                        log.info("📥 Обработан callback '{}', вызвано меню {}", key, def.getClass().getSimpleName());
                    } catch (Exception e) {
                        log.error("❌ Ошибка при обработке callback {} в {}", key, def.getClass().getSimpleName(), e);
                    }
                    return;
                }
            }

            log.warn("⚠️ Нет MenuDefinition для callback '{}'", key);
            return;
        }

        for (MessageHandler handler : handlers) {
            if (handler.canHandle(update)) {
                try {
                    handler.handle(update);
                } catch (Exception e) {
                    log.error("Ошибка в MessageHandler {}", handler.getClass().getSimpleName(), e);
                }
                return;
            }
        }

        log.warn("❓ Не найден обработчик для update: {}", update);
    }
}
