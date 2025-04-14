package com.chicu.neurotradebot.telegram.callback;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CallbackProcessor {
    BotCallback callback();

    void process(Long chatId, Integer messageId, String callbackData, AbsSender sender);
}
