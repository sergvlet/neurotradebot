package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.handler.api.ApiKeySetupHandler;
import com.chicu.neurotradebot.telegram.handler.exchange.ExchangeSelectionHandler;
import com.chicu.neurotradebot.telegram.handler.main.MainMenuHandler;
import com.chicu.neurotradebot.telegram.handler.menu.SubscriptionMenuBuilder;
import com.chicu.neurotradebot.telegram.handler.settings.SettingsMenuHandler;
import com.chicu.neurotradebot.telegram.handler.trade.TradeMenuHandler;
import com.chicu.neurotradebot.telegram.handler.trade.ai.AITradeMenuBuilder;
import com.chicu.neurotradebot.telegram.handler.trade.ai.AITradeMenuHandler;
import com.chicu.neurotradebot.telegram.session.InputStage;
import com.chicu.neurotradebot.telegram.session.UserSessionManager;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.user.service.AiTradeSettingsSyncService;
import com.chicu.neurotradebot.user.service.SubscriptionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TelegramUpdateHandler {

    private final MainMenuHandler mainMenuHandler;
    private final TradeMenuHandler tradeMenuHandler;
    private final SettingsMenuHandler settingsMenuHandler;
    private final ExchangeSelectionHandler exchangeSelectionHandler;
    private final ApiKeySetupHandler apiKeySetupHandler;
    private final AITradeMenuHandler aiTradeMenuHandler;
    private final AITradeMenuBuilder aiTradeMenuBuilder;
    private final UserRepository userRepository;
    private final SubscriptionChecker subscriptionChecker;
    private final SubscriptionMenuBuilder subscriptionMenuBuilder;
    private final AiTradeSettingsSyncService aiTradeSettingsSyncService;

    public Object handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            return handleCallbackQuery(update);
        }
        return null;
    }

    private Object handleMessage(Message message) {
        long chatId = message.getChatId();
        String text = message.getText();

        ensureUserExists(message);

        InputStage inputStage = UserSessionManager.getInputStage(chatId);
        if (inputStage != InputStage.NONE) {
            Update fakeUpdate = new Update();
            fakeUpdate.setMessage(message);

            if (inputStage == InputStage.AI_LIST_ADD) {
                return handleAiListAdd(fakeUpdate);
            }

            return apiKeySetupHandler.handle(fakeUpdate);
        }

        if ("/start".equals(text)) {
            return mainMenuHandler.startNewMessage(message);
        }

        return null;
    }

    private Object handleCallbackQuery(Update update) {
        var callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        ensureUserExists(callbackQuery.getMessage());

        if (data.equals("subscribe_menu")) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("🛡 *Выберите подходящий тариф:*")
                    .replyMarkup(subscriptionMenuBuilder.buildSubscriptionMenu())
                    .parseMode("Markdown")
                    .build();
        }

        if (data.startsWith("trade_")) {
            return tradeMenuHandler.handle(update);
        }

        if (data.startsWith("settings_") || data.equals("switch_mode") ||
            data.equals("back_to_settings") || data.equals("select_manual_mode") || data.equals("select_ai_mode")) {
            return settingsMenuHandler.handle(update);
        }

        if (data.startsWith("ai_")) {
            return aiTradeMenuHandler.handle(update);
        }

        if (data.startsWith("select_")) {
            return exchangeSelectionHandler.handle(update);
        }

        if (data.startsWith("api_setup_") || data.startsWith("api_replace_") || data.equals("api_setup_start")) {
            return apiKeySetupHandler.handle(update);
        }

        if (data.equals("back_to_main")) {
            return mainMenuHandler.editStartMenu(update);
        }

        return null;
    }

    private Object handleAiListAdd(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText().replaceAll("\\s+", "");

        Integer messageId = UserSessionManager.getLastBotMessageId(chatId);
        if (messageId == null) {
            return null;
        }

        if (!text.contains("/")) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("⚠️ Неверный формат. Пример правильного ввода:\n\n`BTC/USDT,ETH/USDT`")
                    .parseMode("Markdown")
                    .build();
        }

        UserSessionManager.setAiAllowedPairs(chatId, text.toUpperCase());
        aiTradeSettingsSyncService.saveSessionToDb(chatId);
        UserSessionManager.setInputStage(chatId, InputStage.NONE);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("✅ Список пар обновлён:\n\n" + text.toUpperCase())
                .replyMarkup(aiTradeMenuBuilder.buildListPairMenu())
                .parseMode("Markdown")
                .build();
    }

    private void ensureUserExists(Message message) {
        Long chatId = message.getChatId();
        if (!userRepository.existsById(chatId)) {
            User newUser = new User();
            newUser.setId(chatId);
            newUser.setUsername(message.getFrom().getUserName());
            newUser.setFirstName(message.getFrom().getFirstName());
            newUser.setLastName(message.getFrom().getLastName());
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setSubscriptionStartAt(null);
            newUser.setSubscriptionEndAt(null);
            newUser.setTrialUsed(false);
            userRepository.save(newUser);
        }
    }
}
