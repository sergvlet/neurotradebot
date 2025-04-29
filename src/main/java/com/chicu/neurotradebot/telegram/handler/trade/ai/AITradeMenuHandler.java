package com.chicu.neurotradebot.telegram.handler.trade.ai;

import com.chicu.neurotradebot.telegram.session.InputStage;
import com.chicu.neurotradebot.telegram.session.UserSessionManager;
import com.chicu.neurotradebot.user.service.AiTradeSettingsSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AITradeMenuHandler {

    private final AITradeMenuBuilder aiTradeMenuBuilder;
    private final AiTradeSettingsSyncService aiTradeSettingsSyncService;

    public Object showMainMenu(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        aiTradeSettingsSyncService.loadSettingsToSession(chatId);

        String tradingType = UserSessionManager.getAiTradingType(chatId);
        String strategy = UserSessionManager.getAiStrategy(chatId);
        String risk = UserSessionManager.getAiRiskLevel(chatId);
        String autostart = UserSessionManager.isAiAutostart(chatId) ? "Включён" : "Отключён";
        String notifications = UserSessionManager.isAiNotifications(chatId) ? "Включены" : "Отключены";
        String pairMode = UserSessionManager.getAiPairMode(chatId);
        String manualPair = UserSessionManager.getAiManualPair(chatId);
        String allowedPairs = UserSessionManager.getAiAllowedPairs(chatId);

        String pairInfo = switch (pairMode) {
            case "MANUAL" -> "Ручной (пара: *%s*)".formatted(manualPair);
            case "LIST" -> "Список пар: *%s*".formatted(allowedPairs.isEmpty() ? "не задан" : allowedPairs);
            case "AUTO" -> "Автоматический выбор AI";
            default -> "Не задан";
        };

        String currentSettings = """
            🤖 *AI-торговля: настройки*

            Тип торговли: *%s*
            Стратегия: *%s*
            Риск: *%s*
            Автостарт: *%s*
            Уведомления: *%s*
            Валютная пара: %s

            Выберите, что хотите изменить:
            """.formatted(tradingType, strategy, risk, autostart, notifications, pairInfo);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(currentSettings)
                .replyMarkup(aiTradeMenuBuilder.buildMainMenu())
                .parseMode("Markdown")
                .build();
    }

    public Object handle(Update update) {
        String data = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        // выбор ручной пары
        switch (data) {
            case "ai_manual_pair_BTCUSDT" -> {
                return saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "BTC/USDT"), chatId, update);
            }
            case "ai_manual_pair_ETHUSDT" -> {
                return saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "ETH/USDT"), chatId, update);
            }
            case "ai_manual_pair_BNBUSDT" -> {
                return saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "BNB/USDT"), chatId, update);
            }
            case "ai_manual_pair_SOLUSDT" -> {
                return saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "SOL/USDT"), chatId, update);
            }
            case "ai_manual_pair_XRPUSDT" -> {
                return saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "XRP/USDT"), chatId, update);
            }
            case "ai_manual_pair_ADAUSDT" -> {
                return saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "ADA/USDT"), chatId, update);
            }
        }

        // выбор пары из списка
        if (data.startsWith("ai_list_select_")) {
            String raw = data.replace("ai_list_select_", "");
            String formatted = raw.replaceAll("(?<=\\w{3})(?=\\w{3}$)", "/").toUpperCase();
            UserSessionManager.setAiManualPair(chatId, formatted);
            aiTradeSettingsSyncService.saveSessionToDb(chatId);
            return showMainMenu(update);
        }

        // удаление пары из списка
        if (data.startsWith("ai_list_del_")) {
            String raw = data.replace("ai_list_del_", "");
            String formatted = raw.replaceAll("(?<=\\w{3})(?=\\w{3}$)", "/").toUpperCase();
            UserSessionManager.removeAiAllowedPair(chatId, formatted);
            aiTradeSettingsSyncService.saveSessionToDb(chatId);
            return showRemoveListInstruction(update);
        }

        return switch (data) {
            case "ai_trading_type" -> showTradingTypeMenu(update);
            case "ai_strategy" -> showStrategySelection(update);
            case "ai_risk" -> showRiskSelection(update);
            case "ai_autostart" -> showAutostartMenu(update);
            case "ai_notifications" -> showNotificationsMenu(update);
            case "ai_pair" -> showPairSelectionMenu(update);
            case "ai_back_main" -> showMainMenu(update);
            case "ai_pair_mode_manual" -> saveAndReturn(() -> UserSessionManager.setAiPairMode(chatId, "MANUAL"), chatId, update);
            case "ai_pair_mode_list" -> saveAndReturn(() -> UserSessionManager.setAiPairMode(chatId, "LIST"), chatId, update);
            case "ai_pair_mode_auto" -> saveAndReturn(() -> UserSessionManager.setAiPairMode(chatId, "AUTO"), chatId, update);
            case "ai_list_add" -> showAddListInstruction(update);
            case "ai_list_remove" -> showRemoveListInstruction(update);
            case "ai_list_pick" -> showAllowedPairsMenu(update);
            default -> null;
        };
    }

    private Object showTradingTypeMenu(Update update) {
        return buildEditMessage(update, "📈 Выберите тип торговли:", aiTradeMenuBuilder.buildTradingTypeMenu());
    }

    private Object showStrategySelection(Update update) {
        return buildEditMessage(update, "🎯 Выберите стратегию AI:", aiTradeMenuBuilder.buildStrategySelectionMenu());
    }

    private Object showRiskSelection(Update update) {
        return buildEditMessage(update, "⚖️ Выберите уровень риска:", aiTradeMenuBuilder.buildRiskSelectionMenu());
    }

    private Object showAutostartMenu(Update update) {
        return buildEditMessage(update, "🚀 Автостарт торговли:", aiTradeMenuBuilder.buildAutoStartMenu());
    }

    private Object showNotificationsMenu(Update update) {
        return buildEditMessage(update, "🔔 Настройки уведомлений:", aiTradeMenuBuilder.buildNotificationsMenu());
    }

    private Object showPairSelectionMenu(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String mode = UserSessionManager.getAiPairMode(chatId);
        String text = "💱 *Выберите режим выбора валютной пары:*\n\nТекущий режим: *" + mode + "*";
        return buildEditMessage(update, text, aiTradeMenuBuilder.buildPairSelectionMenu());
    }

    private Object showManualPairMenu(Update update) {
        return buildEditMessage(update, "💱 Выберите валютную пару:", aiTradeMenuBuilder.buildManualPairSelectionMenu());
    }

    private Object showListPairMenu(Update update) {
        return buildEditMessage(update, "📋 Настройки списка валютных пар:", aiTradeMenuBuilder.buildListPairMenu());
    }

    private Object showAddListInstruction(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        UserSessionManager.setInputStage(chatId, InputStage.AI_LIST_ADD);
        UserSessionManager.setLastBotMessageId(chatId, messageId);
        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("➕ *Пришлите пары через запятую, например:*\n\n`BTC/USDT,ETH/USDT`")
                .parseMode("Markdown")
                .build();
    }

    private Object showRemoveListInstruction(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String allowed = UserSessionManager.getAiAllowedPairs(chatId);
        if (allowed.isEmpty()) {
            return buildEditMessage(update, "📭 Список пуст. Нечего удалять.", aiTradeMenuBuilder.buildListPairMenu());
        }
        return buildEditMessage(update, "➖ *Выберите пару для удаления:*", aiTradeMenuBuilder.buildRemovePairsMenu(allowed));
    }

    private Object showAllowedPairsMenu(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String allowedPairs = UserSessionManager.getAiAllowedPairs(chatId);
        if (allowedPairs.isEmpty()) {
            return buildEditMessage(update, "📭 Нет доступных пар для выбора!", aiTradeMenuBuilder.buildListPairMenu());
        }
        return buildEditMessage(update, "📜 *Выберите валютную пару из списка:*", aiTradeMenuBuilder.buildAllowedPairsMenu(allowedPairs));
    }

    private Object saveAndReturn(Runnable sessionAction, Long chatId, Update update) {
        sessionAction.run();
        aiTradeSettingsSyncService.saveSessionToDb(chatId);
        return showMainMenu(update);
    }

    private Object buildEditMessage(Update update, String text, org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup markup) {
        return EditMessageText.builder()
                .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .text(text)
                .replyMarkup(markup)
                .parseMode("Markdown")
                .build();
    }
}
