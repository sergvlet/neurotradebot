// src/main/java/com/chicu/neurotradebot/telegram/handler/TextInputAwareMenu.java
package com.chicu.neurotradebot.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для MenuDefinition, которые могут обрабатывать текстовый ввод от пользователя.
 */
public interface TextInputAwareMenu {
    void handleText(Update update) throws Exception;
}
