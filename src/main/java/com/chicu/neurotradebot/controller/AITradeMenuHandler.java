package com.chicu.neurotradebot.controller;

import com.chicu.neurotradebot.session.InputStage;
import com.chicu.neurotradebot.session.UserSessionManager;
import com.chicu.neurotradebot.service.AiTradeSettingsSyncService;
import com.chicu.neurotradebot.view.AITradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

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

        String pairInfo = switch (pairMode) {
            case "MANUAL" -> "Ручной (пара: *%s*)".formatted(manualPair);
            case "LIST" -> {
                String list = manualPair != null && !manualPair.isBlank() ? manualPair : "не выбран";
                yield "Список: *%s*".formatted(list);
            }
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

        return switch (data) {
            case "ai_trading_type" -> showTradingTypeMenu(update);
            case "ai_strategy" -> showStrategySelection(update);
            case "ai_risk" -> showRiskSelection(update);
            case "ai_autostart" -> showAutostartMenu(update);
            case "ai_notifications" -> showNotificationsMenu(update);
            case "ai_pair" -> showPairSelectionMenu(update);
            case "ai_back_main" -> showMainMenu(update);
            case "ai_back_list_menu" -> showListPairMenu(update);

            case "ai_pair_mode_manual" -> {
                UserSessionManager.setAiPairMode(chatId, "MANUAL");
                aiTradeSettingsSyncService.saveSessionToDb(chatId);
                yield showManualPairMenu(update);
            }
            case "ai_pair_mode_list" -> {
                UserSessionManager.setAiPairMode(chatId, "LIST");
                aiTradeSettingsSyncService.saveSessionToDb(chatId);
                yield showListPairMenu(update);
            }
            case "ai_pair_mode_auto" -> {
                UserSessionManager.setAiPairMode(chatId, "AUTO");
                aiTradeSettingsSyncService.saveSessionToDb(chatId);
                yield showMainMenu(update);
            }

            case "ai_manual_pair_BTCUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "BTC/USDT"), chatId, update);
            case "ai_manual_pair_ETHUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "ETH/USDT"), chatId, update);
            case "ai_manual_pair_BNBUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "BNB/USDT"), chatId, update);
            case "ai_manual_pair_SOLUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "SOL/USDT"), chatId, update);
            case "ai_manual_pair_XRPUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "XRP/USDT"), chatId, update);
            case "ai_manual_pair_ADAUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "ADA/USDT"), chatId, update);

            case "ai_list_add" -> showAddListInstruction(update);
            case "ai_list_remove" -> showRemoveListInstruction(update);
            case "ai_list_pick" -> showListSelectMenu(update);

            default -> {
                if (data.startsWith("ai_list_select_")) {
                    int index = Integer.parseInt(data.replace("ai_list_select_", ""));
                    String selectedList = UserSessionManager.getAiAllowedPairsList(chatId).get(index);
                    UserSessionManager.setAiManualPair(chatId, selectedList);
                    aiTradeSettingsSyncService.saveSessionToDb(chatId);
                    yield showMainMenu(update);
                }
                if (data.startsWith("ai_list_del_item_")) {
                    int index = Integer.parseInt(data.replace("ai_list_del_item_", ""));
                    UserSessionManager.removeAiAllowedPairAt(chatId, index);
                    aiTradeSettingsSyncService.saveSessionToDb(chatId);
                    yield showRemoveListInstruction(update);
                }
                yield null;
            }
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
        return buildEditMessage(update,
                "💱 *Выберите режим выбора валютной пары:*\n\nТекущий режим: *" + mode + "*",
                aiTradeMenuBuilder.buildPairSelectionMenu());
    }

    private Object showManualPairMenu(Update update) {
        return buildEditMessage(update, "💱 Выберите валютную пару:", aiTradeMenuBuilder.buildManualPairSelectionMenu());
    }

    private Object showListPairMenu(Update update) {
        return buildEditMessage(update, "📋 Списки валютных пар:", aiTradeMenuBuilder.buildListPairMenu());
    }

    private Object showListSelectMenu(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        List<String> allLists = UserSessionManager.getAiAllowedPairsList(chatId);
        return buildEditMessage(update, "📜 Выберите список для торговли:", aiTradeMenuBuilder.buildListSelectMenu(allLists));
    }

    private Object showRemoveListInstruction(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        List<String> allLists = UserSessionManager.getAiAllowedPairsList(chatId);
        if (allLists.isEmpty()) {
            return buildEditMessage(update, "📭 Нет сохранённых списков.", aiTradeMenuBuilder.buildListPairMenu());
        }
        return buildEditMessage(update, "➖ Выберите список для удаления:", aiTradeMenuBuilder.buildListRemoveMenu(allLists));
    }

    private Object showAddListInstruction(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        UserSessionManager.setInputStage(chatId, InputStage.AI_LIST_ADD);
        UserSessionManager.setLastBotMessageId(chatId, messageId);
        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("➕ *Пришлите пары через запятую (одна строка — один список):*\n\n`BTC/USDT,ETH/USDT`")
                .replyMarkup(aiTradeMenuBuilder.buildListPairMenu())
                .parseMode("Markdown")
                .build();
    }

    public List<Object> handleAiListAdd(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText().replaceAll("\\s+", "").toUpperCase();
        List<Object> actions = new ArrayList<>();

        actions.add(DeleteMessage.builder()
                .chatId(String.valueOf(chatId))
                .messageId(message.getMessageId())
                .build());

        int replyTo = UserSessionManager.getLastBotMessageId(chatId);

        if (!text.contains("/")) {
            actions.add(EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(replyTo)
                    .text("⚠️ *Неверный формат.* Пример:\n\n`BTC/USDT,ETH/USDT`")
                    .replyMarkup(aiTradeMenuBuilder.buildListPairMenu())
                    .parseMode("Markdown")
                    .build());
            return actions;
        }

        UserSessionManager.appendAiAllowedPair(chatId, text);
        aiTradeSettingsSyncService.saveSessionToDb(chatId);
        UserSessionManager.setInputStage(chatId, InputStage.NONE);

        List<String> allLists = UserSessionManager.getAiAllowedPairsList(chatId);

        actions.add(EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(replyTo)
                .text("📜 *Выберите валютную пару из списка:*")
                .replyMarkup(aiTradeMenuBuilder.buildListSelectMenu(allLists))
                .parseMode("Markdown")
                .build());

        return actions;
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

    private Object saveAndReturn(Runnable sessionAction, Long chatId, Update update) {
        sessionAction.run();
        aiTradeSettingsSyncService.saveSessionToDb(chatId);
        return showMainMenu(update);
    }
}
