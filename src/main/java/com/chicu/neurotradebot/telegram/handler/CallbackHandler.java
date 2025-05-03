// src/main/java/com/chicu/neurotradebot/telegram/handler/CallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {
    /** Подходит ли этот хендлер для данного callbackData? */
    boolean canHandle(CallbackQuery query);
    /** Логика обработки: редактирование/отправка меню и т.п. */
    void handle(CallbackQuery query) throws Exception;
}
