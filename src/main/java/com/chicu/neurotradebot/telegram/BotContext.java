package com.chicu.neurotradebot.telegram;

public class BotContext {

    private static final ThreadLocal<Long> chatIdHolder = new ThreadLocal<>();

    public static void setChatId(Long chatId) {
        chatIdHolder.set(chatId);
    }

    public static Long getChatId() {
        return chatIdHolder.get();
    }

    public static void clear() {
        chatIdHolder.remove();
    }
}
