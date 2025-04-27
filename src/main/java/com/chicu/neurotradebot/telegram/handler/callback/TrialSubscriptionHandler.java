package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.SubscriptionService;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class TrialSubscriptionHandler {

    private final SubscriptionService subscriptionService;
    private final StartMenuBuilder startMenuBuilder;

    public EditMessageText handle(long chatId, Integer messageId) {
        try {
            subscriptionService.createTrialSubscription(chatId);

            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                        🎁 Бесплатная подписка активирована на 7 дней!

                        Теперь у вас есть доступ к функциям бота.
                        Удачной торговли! 🚀
                        """)
                    .replyMarkup(startMenuBuilder.buildMainMenu())
                    .parseMode("Markdown")
                    .build();

        } catch (Exception e) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("⚠️ Ошибка активации бесплатной подписки: " + e.getMessage())
                    .build();
        }
    }
}
