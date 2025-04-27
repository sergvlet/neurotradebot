package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.subscription.entity.Subscription;
import com.chicu.neurotradebot.subscription.service.SubscriptionService;
import com.chicu.neurotradebot.telegram.handler.callback.*;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TelegramCallbackDispatcher {

    private final TradeCallbackHandler tradeHandler;
    private final SettingsCallbackHandler settingsHandler;
    private final StatsCallbackHandler statsHandler;
    private final BalanceCallbackHandler balanceHandler;
    private final TradingModeSettingsHandler tradingModeSettingsHandler;
    private final SelectManualTradingHandler selectManualTradingHandler;
    private final SelectAiTradingHandler selectAiTradingHandler;
    private final ToggleRealTradingHandler toggleRealTradingHandler;
    private final ToggleTestTradingHandler toggleTestTradingHandler;
    private final ChooseLaterHandler chooseLaterHandler;
    private final SubscriptionSelectHandler subscriptionSelectHandler;
    private final PaymentMethodSelectHandler paymentMethodSelectHandler;
    private final TrialSubscriptionHandler trialSubscriptionHandler;
    private final PaidPlanSelectHandler paidPlanSelectHandler;
    private final SubscriptionService subscriptionService;
    private final ConfirmPaymentHandler confirmPaymentHandler;
    private final SelectCoinHandler selectCoinHandler;
    private final ToggleTradingModeHandler toggleTradingModeHandler;
    private final ApiKeySetupHandler apiKeySetupHandler;
    private final ExchangeSelectHandler exchangeSelectHandler;

    public BotApiMethod<?> handle(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        return switch (callbackData) {
            case "BALANCE" -> balanceHandler.handle(chatId, messageId);
            case "TRADE" -> tradeHandler.handle(chatId, messageId);
            case "SETTINGS" -> settingsHandler.handle(chatId, messageId);
            case "REAL_TRADING" -> toggleRealTradingHandler.handle(chatId, messageId);
            case "TEST_TRADING" -> toggleTestTradingHandler.handle(chatId, messageId);
            case "TRADING_MODE" -> tradingModeSettingsHandler.handle(chatId, messageId);
            case "MANUAL_TRADING" -> selectManualTradingHandler.handle(chatId, messageId);
            case "AI_TRADING" -> selectAiTradingHandler.handle(chatId, messageId);
            case "SUBSCRIPTION" -> subscriptionSelectHandler.handle(chatId, messageId);
            case "TRIAL_SUBSCRIPTION" -> trialSubscriptionHandler.handle(chatId, messageId);
            case "PAID_SUBSCRIPTION", "RENEW_SUBSCRIPTION" -> paidPlanSelectHandler.handle(chatId, messageId);
            case "PLAN_1M" -> handlePaidPlan(chatId, messageId, Subscription.PlanType.MONTHLY);
            case "PLAN_3M" -> handlePaidPlan(chatId, messageId, Subscription.PlanType.QUARTERLY);
            case "PLAN_6M" -> handlePaidPlan(chatId, messageId, Subscription.PlanType.HALF_YEAR);
            case "PLAN_12M" -> handlePaidPlan(chatId, messageId, Subscription.PlanType.YEARLY);
            case "CONFIRM_PAYMENT" -> confirmPaymentHandler.handle(chatId, messageId);
            case "BUY_COIN" -> selectCoinHandler.handle(chatId, messageId);
            case "CHOOSE_LATER" -> chooseLaterHandler.handle(chatId, messageId);
            case "STATS" -> statsHandler.handle(chatId, messageId);
            case "TOGGLE_TRADING_MODE" -> toggleTradingModeHandler.handle(chatId, messageId);
            case "SETUP_API_KEYS" -> apiKeySetupHandler.handle(update);
            case "EXCHANGE_BINANCE", "EXCHANGE_BYBIT", "EXCHANGE_KUCOIN" -> exchangeSelectHandler.handleEdit(update);

            default -> EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("⚠️ Неизвестная команда. Пожалуйста, используйте меню.")
                    .parseMode("Markdown")
                    .build();
        };
    }

    private EditMessageText handlePaidPlan(Long chatId, Integer messageId, Subscription.PlanType planType) {
        subscriptionService.createPendingSubscription(chatId, planType);
        return paymentMethodSelectHandler.handle(chatId, messageId);
    }
}
