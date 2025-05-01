package com.chicu.neurotradebot.controller;

import com.chicu.neurotradebot.model.AiTradeSettings;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.AiVisualizationService;
import com.chicu.neurotradebot.session.InputStage;
import com.chicu.neurotradebot.session.UserSessionManager;
import com.chicu.neurotradebot.service.AiTradeSettingsSyncService;
import com.chicu.neurotradebot.strategy.RsiEmaStrategyMenuHandler;
import com.chicu.neurotradebot.view.AITradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AITradeMenuHandler {

    private final AITradeMenuBuilder aiTradeMenuBuilder;
    private final AiTradeSettingsSyncService aiTradeSettingsSyncService;
    private final RsiEmaStrategyMenuHandler rsiEmaStrategyMenuHandler;
    private final AiVisualizationService aiVisualizationService;
    private final AiTradeSettingsService aiTradeSettingsService;




    public Object showMainMenu(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        aiTradeSettingsSyncService.loadSettingsToSession(chatId);

        String tradingType = UserSessionManager.getAiTradingType(chatId);
        String strategy = UserSessionManager.getAiStrategy(chatId);
        String risk = UserSessionManager.getAiRiskLevel(chatId);
        String notifications = UserSessionManager.isAiNotifications(chatId) ? "Включены" : "Отключены";
        boolean aiRunning = UserSessionManager.isAiRunning(chatId);
        String aiStatus = aiRunning ? "✅ *ВКЛЮЧЕНА*" : "⛔ *ВЫКЛЮЧЕНА*";

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

            Статус AI: %s
            Тип торговли: *%s*
            Стратегия: *%s*
            Риск: *%s*
            Уведомления: *%s*
            Валютная пара: %s

            Выберите, что хотите изменить:
            """.formatted(aiStatus, tradingType, strategy, risk, notifications, pairInfo);

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

        Object result = switch (data) {
            case "ai_visual" -> showVisualizationMessage(update);
            case "ai_back_main_from_chart" -> {
                int chartMsgId = UserSessionManager.getLastChartMessageId(chatId);
                DeleteMessage deleteChart = DeleteMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(chartMsgId)
                        .build();

                SendMessage mainMenu = SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text(buildCurrentSettingsMessage(chatId))
                        .replyMarkup(aiTradeMenuBuilder.buildMainMenu())
                        .parseMode("Markdown")
                        .build();

                yield List.of(deleteChart, mainMenu);
            }



            case "ai_back_main" -> showMainMenu(update);

            case "ai_trading_type" -> showTradingTypeMenu(update);
            case "ai_strategy" -> showStrategySelection(update);
            case "ai_risk" -> showRiskSelection(update);
            case "ai_notifications" -> showNotificationsMenu(update);
            case "ai_pair" -> showPairSelectionMenu(update);
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

            case "ai_strategy_rsi_ema", "ai_strategy_main" -> rsiEmaStrategyMenuHandler.showStrategyOptions(update);

            case "ai_manual_pair_BTCUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "BTC/USDT"), chatId, update);
            case "ai_manual_pair_ETHUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "ETH/USDT"), chatId, update);
            case "ai_manual_pair_BNBUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "BNB/USDT"), chatId, update);
            case "ai_manual_pair_SOLUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "SOL/USDT"), chatId, update);
            case "ai_manual_pair_XRPUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "XRP/USDT"), chatId, update);
            case "ai_manual_pair_ADAUSDT" -> saveAndReturn(() -> UserSessionManager.setAiManualPair(chatId, "ADA/USDT"), chatId, update);

            case "ai_list_add" -> showAddListInstruction(update);
            case "ai_list_remove" -> showRemoveListInstruction(update);
            case "ai_list_pick" -> showListSelectMenu(update);

            case "ai_strategy_settings", "ai_set_rsi_period", "ai_set_ema_short",
                 "ai_set_ema_long", "ai_set_rsi_buy", "ai_set_rsi_sell", "ai_set_limit"
                    -> rsiEmaStrategyMenuHandler.handleCallback(update);
            case "ai_strategy_reset" -> rsiEmaStrategyMenuHandler.resetToDefault(update);

            case "ai_control" -> {
                boolean running = UserSessionManager.isAiRunning(chatId);
                yield buildEditMessage(update, "🤖 Управление AI-режимом:", aiTradeMenuBuilder.buildStartStopMenu(running));
            }
            case "ai_start" -> saveAndReturn(() -> UserSessionManager.setAiRunning(chatId, true), chatId, update);
            case "ai_stop" -> saveAndReturn(() -> UserSessionManager.setAiRunning(chatId, false), chatId, update);

            default -> null;
        };

        if (result != null) return result;

        // списки
        if (data.startsWith("ai_list_select_")) {
            int index = Integer.parseInt(data.replace("ai_list_select_", ""));
            String selectedList = UserSessionManager.getAiAllowedPairsList(chatId).get(index);
            UserSessionManager.setAiManualPair(chatId, selectedList);
            aiTradeSettingsSyncService.saveSessionToDb(chatId);
            return showMainMenu(update);
        }

        if (data.startsWith("ai_list_del_item_")) {
            int index = Integer.parseInt(data.replace("ai_list_del_item_", ""));
            UserSessionManager.removeAiAllowedPairAt(chatId, index);
            aiTradeSettingsSyncService.saveSessionToDb(chatId);
            return showRemoveListInstruction(update);
        }

        if (data.startsWith("ai_strategy_")) {
            String value = data.replace("ai_strategy_", "");
            return saveAndReturn(() -> UserSessionManager.setAiStrategy(chatId, value), chatId, update);
        }

        if (data.startsWith("ai_risk_")) {
            String value = data.replace("ai_risk_", "");
            return saveAndReturn(() -> UserSessionManager.setAiRiskLevel(chatId, value), chatId, update);
        }

        if (data.startsWith("ai_trading_type_")) {
            String value = data.replace("ai_trading_type_", "");
            return saveAndReturn(() -> UserSessionManager.setAiTradingType(chatId, value), chatId, update);
        }

        if (data.startsWith("ai_notifications_")) {
            boolean enabled = data.endsWith("_on");
            return saveAndReturn(() -> UserSessionManager.setAiNotifications(chatId, enabled), chatId, update);
        }

        return null;
    }

    private Object showVisualizationMessage(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        AiTradeSettings settings = aiTradeSettingsService
                .createIfAbsentAndInitializeDefaults(chatId); // гарантированная загрузка

        try {
            byte[] chartImage = aiVisualizationService.generateChart(settings);

            // Создаём временный PNG-файл
            File tempFile = File.createTempFile("chart_", ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(chartImage);
            }

            // Создаём сообщение с графиком
            SendPhoto photo = SendPhoto.builder()
                    .chatId(String.valueOf(chatId))
                    .photo(new InputFile(tempFile))
                    .caption("📊 Визуализация стратегии *%s* по паре *%s*".formatted(
                            settings.getStrategy(),
                            settings.getManualPair()
                    ))
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(List.of(
                                    List.of(InlineKeyboardButton.builder()
                                            .text("🔙 Назад")
                                            .callbackData("ai_back_main_from_chart")
                                            .build())
                            ))
                            .build())
                    .parseMode("Markdown")
                    .build();

            // Удаляем старое меню
            DeleteMessage delete = DeleteMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .build();

            return List.of(delete, photo);

        } catch (Exception e) {
            log.error("Ошибка при визуализации: {}", e.getMessage(), e);

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("❌ Не удалось построить график. Попробуйте позже.")
                    .replyMarkup(aiTradeMenuBuilder.buildBackToMainMenu())
                    .parseMode("Markdown")
                    .build();

        }

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

    private String buildCurrentSettingsMessage(long chatId) {
        String tradingType = UserSessionManager.getAiTradingType(chatId);
        String strategy = UserSessionManager.getAiStrategy(chatId);
        String risk = UserSessionManager.getAiRiskLevel(chatId);
        String notifications = UserSessionManager.isAiNotifications(chatId) ? "Включены" : "Отключены";
        boolean aiRunning = UserSessionManager.isAiRunning(chatId);
        String aiStatus = aiRunning ? "✅ ВКЛЮЧЕНА" : "⛔ ВЫКЛЮЧЕНА";

        String pairMode = UserSessionManager.getAiPairMode(chatId);
        String manualPair = UserSessionManager.getAiManualPair(chatId);

        String pairInfo = switch (pairMode) {
            case "MANUAL" -> "Ручной (пара: %s)".formatted(manualPair);
            case "LIST" -> {
                String list = manualPair != null && !manualPair.isBlank() ? manualPair : "не выбран";
                yield "Список: %s".formatted(list);
            }
            case "AUTO" -> "Автоматический выбор AI";
            default -> "Не задан";
        };

        return """
        🤖 *AI-торговля: настройки*

        Статус AI: %s
        Тип торговли: *%s*
        Стратегия: *%s*
        Риск: *%s*
        Уведомления: *%s*
        Валютная пара: %s

        Выберите, что хотите изменить:
        """.formatted(aiStatus, tradingType, strategy, risk, notifications, pairInfo);
    }


}
