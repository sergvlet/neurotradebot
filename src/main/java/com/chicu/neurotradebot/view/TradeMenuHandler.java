package com.chicu.neurotradebot.view;

import com.chicu.neurotradebot.service.SubscriptionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TradeMenuHandler {

    private final TradeMenuBuilder tradeMenuBuilder;
    private final SubscriptionChecker subscriptionChecker;

    public Object handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        if (!subscriptionChecker.hasActiveSubscription(chatId)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                          🔒 Для полноценного использования NeuroTradeBot
                          необходимо пройти регистрацию и активировать подписку.

                          Пожалуйста, зарегистрируйтесь через нашу платформу.
                          """)
                    .parseMode("Markdown")
                    .build();
        }

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                      🚀 *Торговля*

                      Выберите действие:
                      """)
                .replyMarkup(tradeMenuBuilder.buildTradeMenu())
                .parseMode("Markdown")
                .build();
    }
}
