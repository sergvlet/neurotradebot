package com.chicu.neurotradebot.session;

import com.chicu.neurotradebot.model.TradeMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionManager {

    // === Общие состояния пользователей ===

    private static final Map<Long, Boolean> userTestnetStatus = new ConcurrentHashMap<>();
    private static final Map<Long, String> userSelectedExchange = new ConcurrentHashMap<>();
    private static final Map<Long, InputStage> inputStages = new ConcurrentHashMap<>();
    private static final Map<Long, String> tempApiKeys = new ConcurrentHashMap<>();
    private static final Map<Long, TradeMode> userTradeMode = new ConcurrentHashMap<>();

    // === AI-торговля состояния ===

    private static final Map<Long, String> aiTradingType = new ConcurrentHashMap<>();
    private static final Map<Long, String> aiStrategy = new ConcurrentHashMap<>();
    private static final Map<Long, String> aiRisk = new ConcurrentHashMap<>();
    private static final Map<Long, Boolean> aiAutostart = new ConcurrentHashMap<>();
    private static final Map<Long, Boolean> aiNotifications = new ConcurrentHashMap<>();
    private static final Map<Long, String> aiPairMode = new ConcurrentHashMap<>();
    private static final Map<Long, String> aiManualPair = new ConcurrentHashMap<>();

    // Новый формат — список списков валютных пар
    private static final Map<Long, List<String>> aiAllowedPairLists = new ConcurrentHashMap<>();

    // Техническая служебная информация
    private static final Map<Long, Integer> lastBotMessageIds = new ConcurrentHashMap<>();

    // === Testnet / Exchange ===

    public static boolean isTestnet(Long userId) {
        return userTestnetStatus.getOrDefault(userId, false);
    }

    public static void toggleTestnet(Long userId) {
        userTestnetStatus.put(userId, !isTestnet(userId));
    }

    public static void setTestnet(Long userId, boolean isTestnet) {
        userTestnetStatus.put(userId, isTestnet);
    }

    public static void setSelectedExchange(Long userId, String exchange) {
        userSelectedExchange.put(userId, exchange);
    }

    public static String getSelectedExchange(Long userId) {
        return userSelectedExchange.getOrDefault(userId, "Не выбрана");
    }

    // === Input Stage ===

    public static InputStage getInputStage(Long userId) {
        return inputStages.getOrDefault(userId, InputStage.NONE);
    }

    public static void setInputStage(Long userId, InputStage stage) {
        inputStages.put(userId, stage);
    }

    // === Temp API Keys ===

    public static void saveTempApiKey(Long userId, String apiKey) {
        tempApiKeys.put(userId, apiKey);
    }

    public static String getTempApiKey(Long userId) {
        return tempApiKeys.get(userId);
    }

    public static void clearTempApiKey(Long userId) {
        tempApiKeys.remove(userId);
    }

    // === Режим торговли ===

    public static TradeMode getTradeMode(Long userId) {
        return userTradeMode.getOrDefault(userId, TradeMode.MANUAL);
    }

    public static void setTradeMode(Long userId, TradeMode tradeMode) {
        userTradeMode.put(userId, tradeMode);
    }

    // === AI Торговля: основные настройки ===

    public static String getAiStrategy(Long chatId) {
        return aiStrategy.getOrDefault(chatId, "Сбалансированная");
    }

    public static void setAiStrategy(Long chatId, String strategy) {
        aiStrategy.put(chatId, strategy);
    }

    public static String getAiRiskLevel(Long chatId) {
        return aiRisk.getOrDefault(chatId, "Средний");
    }

    public static void setAiRiskLevel(Long chatId, String risk) {
        aiRisk.put(chatId, risk);
    }

    public static String getAiTradingType(Long chatId) {
        return aiTradingType.getOrDefault(chatId, "Спотовая");
    }

    public static void setAiTradingType(Long chatId, String type) {
        aiTradingType.put(chatId, type);
    }

    public static boolean isAiAutostart(Long chatId) {
        return aiAutostart.getOrDefault(chatId, false);
    }

    public static void setAiAutostart(Long chatId, boolean enabled) {
        aiAutostart.put(chatId, enabled);
    }

    public static boolean isAiNotifications(Long chatId) {
        return aiNotifications.getOrDefault(chatId, true);
    }

    public static void setAiNotifications(Long chatId, boolean enabled) {
        aiNotifications.put(chatId, enabled);
    }

    public static String getAiPairMode(Long chatId) {
        return aiPairMode.getOrDefault(chatId, "MANUAL");
    }

    public static void setAiPairMode(Long chatId, String mode) {
        aiPairMode.put(chatId, mode);
    }

    public static String getAiManualPair(Long chatId) {
        return aiManualPair.getOrDefault(chatId, "BTC/USDT");
    }

    public static void setAiManualPair(Long chatId, String pair) {
        aiManualPair.put(chatId, pair);
    }

    // === AI Торговля: списки валютных пар ===

    public static List<String> getAiAllowedPairsList(Long chatId) {
        return aiAllowedPairLists.getOrDefault(chatId, new ArrayList<>());
    }

    public static void appendAiAllowedPair(Long chatId, String newPairList) {
        aiAllowedPairLists.computeIfAbsent(chatId, k -> new ArrayList<>()).add(newPairList);
    }

    public static void removeAiAllowedPairAt(Long chatId, int index) {
        List<String> list = aiAllowedPairLists.get(chatId);
        if (list != null && index >= 0 && index < list.size()) {
            list.remove(index);
        }
    }

    // === Служебные сообщения ===

    public static void setLastBotMessageId(Long chatId, Integer messageId) {
        lastBotMessageIds.put(chatId, messageId);
    }

    public static Integer getLastBotMessageId(Long chatId) {
        return lastBotMessageIds.getOrDefault(chatId, null);
    }

    public static void clearAiAllowedPairsList(Long chatId) {
        aiAllowedPairLists.remove(chatId);
    }
}
