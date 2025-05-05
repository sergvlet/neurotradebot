// src/main/java/com/chicu/neurotradebot/telegram/handler/CallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {

    boolean canHandle(Update update);

    void handle(Update update) throws Exception;
}
