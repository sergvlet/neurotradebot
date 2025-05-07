// src/main/java/com/chicu/neurotradebot/telegram/BotContext.java
package com.chicu.neurotradebot.telegram;

import org.telegram.telegrambots.meta.api.objects.User;

public class BotContext {

    private static final ThreadLocal<Long> chatIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<User>   tgUserHolder = new ThreadLocal<>();

    /** Устанавливаем оба контекста разом */
    public static void setContext(Long chatId, User tgUser) {
        chatIdHolder.set(chatId);
        tgUserHolder.set(tgUser);
    }

    public static Long getChatId() {
        return chatIdHolder.get();
    }

    public static User getTgUser() {
        return tgUserHolder.get();
    }

    public static void clear() {
        chatIdHolder.remove();
        tgUserHolder.remove();
    }
}
