package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.AccessControlService;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class ChooseLaterHandler {

    private final StartMenuBuilder startMenuBuilder;
    private final AccessControlService accessControlService;


    public EditMessageText handle(long chatId, Integer messageId) {
        if (!accessControlService.hasActiveSubscription(chatId)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                  ⛔ У вас нет активной подписки.

                  Пожалуйста, нажмите 👤 *Подписка* и выберите подходящий тариф.
                  """)
                    .parseMode("Markdown")
                    .build();
        }

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                        ℹ️ Вы можете ознакомиться с ботом.
                        
                        Для полноценного использования функций (торговля, баланс) необходимо оформить подписку.
                        
                        Нажмите 👤 *Подписка*, чтобы вернуться к выбору тарифов.
                        """)
                .replyMarkup(startMenuBuilder.buildMainMenu())
                .parseMode("Markdown")
                .build();
    }
}
