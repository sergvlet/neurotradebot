package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.SubscriptionService;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class ConfirmPaymentHandler {

    private final SubscriptionService subscriptionService;
    private final StartMenuBuilder startMenuBuilder; // ✅ Инжектим главное меню

    public EditMessageText handle(long chatId, Integer messageId) {
        try {
            subscriptionService.confirmPayment(chatId);

            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                          ✅ Подписка успешно активирована!

                          Спасибо за оплату. Вы можете пользоваться всеми функциями бота.
                          """)
                    .replyMarkup(startMenuBuilder.buildMainMenu()) // ✅ Возвращаем в меню
                    .parseMode("Markdown")
                    .build();

        } catch (RuntimeException e) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                          ❗ Ошибка оплаты:

                          """ + e.getMessage())
                    .replyMarkup(startMenuBuilder.buildMainMenu()) // ✅ Возвращаем в меню даже при ошибке
                    .parseMode("Markdown")
                    .build();
        }
    }
}
