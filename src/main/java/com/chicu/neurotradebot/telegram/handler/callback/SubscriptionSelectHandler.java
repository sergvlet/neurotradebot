package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.entity.Subscription;
import com.chicu.neurotradebot.subscription.entity.SubscriptionStatus;
import com.chicu.neurotradebot.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionSelectHandler {

    private final SubscriptionRepository subscriptionRepository;

    public EditMessageText handle(long chatId, Integer messageId) {
        var activeSubscriptions = subscriptionRepository.findAllByUserIdAndStatus(chatId, SubscriptionStatus.ACTIVE);

        String text;
        InlineKeyboardMarkup markup;

        if (!activeSubscriptions.isEmpty() && activeSubscriptions.get(0).getEndDate().isAfter(java.time.LocalDateTime.now())) {
            Subscription subscription = activeSubscriptions.get(0);
            String planName = getPlanName(subscription.getPlan());
            String endDate = subscription.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            text = String.format("""
                    🎫 Ваша подписка:
                    
                    Тип: *%s*
                    Действует до: *%s*
                    
                    Для продления нажмите кнопку ниже. ⬇️
                    """, planName, endDate);

            InlineKeyboardButton renewButton = InlineKeyboardButton.builder()
                    .text("🪙 Продлить подписку")
                    .callbackData("RENEW_SUBSCRIPTION")
                    .build();

            markup = InlineKeyboardMarkup.builder()
                    .keyboard(List.of(
                            List.of(renewButton)
                    ))
                    .build();

        } else {
            text = "🎫 Выберите тип подписки:";

            InlineKeyboardButton trialButton = InlineKeyboardButton.builder()
                    .text("🎁 Бесплатная подписка (7 дней)")
                    .callbackData("TRIAL_SUBSCRIPTION")
                    .build();

            InlineKeyboardButton paidButton = InlineKeyboardButton.builder()
                    .text("🪙 Платные подписки")
                    .callbackData("PAID_SUBSCRIPTION")
                    .build();

            InlineKeyboardButton laterButton = InlineKeyboardButton.builder()
                    .text("⏳ Выбрать позже")
                    .callbackData("CHOOSE_LATER")
                    .build();

            markup = InlineKeyboardMarkup.builder()
                    .keyboard(List.of(
                            List.of(trialButton),
                            List.of(paidButton),
                            List.of(laterButton)
                    ))
                    .build();
        }

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("Markdown")
                .build();
    }

    private String getPlanName(Subscription.PlanType plan) {
        return switch (plan) {
            case FREE_TRIAL -> "Бесплатная подписка (7 дней)";
            case MONTHLY -> "Платная подписка (1 месяц)";
            case QUARTERLY -> "Платная подписка (3 месяца)";
            case HALF_YEAR -> "Платная подписка (6 месяцев)";
            case YEARLY -> "Платная подписка (12 месяцев)";
        };
    }
}
