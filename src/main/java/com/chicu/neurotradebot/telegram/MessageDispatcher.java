package com.chicu.neurotradebot.telegram;

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
        log.warn("Не найден MessageHandler для update: {}", update);
    }
}
