package com.chicu.neurotradebot.telegram.session;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionManager {

    private static final Map<Long, Boolean> userTestnetStatus = new ConcurrentHashMap<>();
    private static final Map<Long, String> userSelectedExchange = new ConcurrentHashMap<>();
    private static final Map<Long, InputStage> inputStages = new ConcurrentHashMap<>();
    private static final Map<Long, String> tempApiKeys = new ConcurrentHashMap<>();
    private static final Map<Long, SendMessage> pendingMessages = new ConcurrentHashMap<>();

    public static boolean isTestnet(Long userId) {
        return userTestnetStatus.getOrDefault(userId, false);
    }

    public static void toggleTestnet(Long userId) {
        boolean current = isTestnet(userId);
        userTestnetStatus.put(userId, !current);
    }

    public static void setSelectedExchange(Long userId, String exchange) {
        userSelectedExchange.put(userId, exchange);
    }

    public static String getSelectedExchange(Long userId) {
        return userSelectedExchange.getOrDefault(userId, "Не выбрана");
    }

    public static InputStage getInputStage(Long userId) {
        return inputStages.getOrDefault(userId, InputStage.NONE);
    }

    public static void setInputStage(Long userId, InputStage stage) {
        inputStages.put(userId, stage);
    }

    public static void saveTempApiKey(Long userId, String apiKey) {
        tempApiKeys.put(userId, apiKey);
    }

    public static String getTempApiKey(Long userId) {
        return tempApiKeys.get(userId);
    }

    public static void clearTempApiKey(Long userId) {
        tempApiKeys.remove(userId);
    }

    // 💬 Ожидающее сообщение (например, меню подписки)
    public static void addPendingAction(Long userId, SendMessage message) {
        pendingMessages.put(userId, message);
    }

    public static SendMessage popPendingAction(Long userId) {
        return pendingMessages.remove(userId);
    }
}
