// src/main/java/com/chicu/neurotradebot/telegram/UpdateDispatcher.java
package com.chicu.neurotradebot.telegram;

import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDispatcher {
    private final List<CallbackHandler> handlers;

    /** Делегирует CallbackQuery первому подходящему CallbackHandler. */
    public void dispatch(CallbackQuery query) {
        log.debug("Получен CallbackQuery: {}", query.getData());
        for (CallbackHandler h : handlers) {
            if (h.canHandle(query)) {
                try {
                    h.handle(query);
                } catch (Exception e) {
                    log.error("Ошибка в обработчике для '{}'", query.getData(), e);
                }
                return;
            }
        }
        log.warn("Нет обработчика для callbackData='{}'", query.getData());
    }
}
