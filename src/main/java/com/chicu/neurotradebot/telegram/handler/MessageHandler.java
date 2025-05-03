// src/main/java/com/chicu/neurotradebot/telegram/handler/MessageHandler.java
package com.chicu.neurotradebot.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Общий интерфейс для разбора приходящих Update.
 */
public interface MessageHandler {
    /**
     * Проверить, может ли этот обработчик обработать текущее update.
     */
    boolean canHandle(Update update);

    /**
     * Обработать update.
     */
    void handle(Update update) throws Exception;
}
