package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.AccessControlService;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
public class StatsCallbackHandler {

    private final StartMenuBuilder menuBuilder;
    private final AccessControlService accessControlService;

    public StatsCallbackHandler(StartMenuBuilder menuBuilder, AccessControlService accessControlService) {
        this.menuBuilder = menuBuilder;
        this.accessControlService = accessControlService;
    }

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
                .text("📈 Раздел статистики. Здесь будет анализ и графики.")
                .replyMarkup(menuBuilder.buildMainMenu())
                .build();
    }
}
