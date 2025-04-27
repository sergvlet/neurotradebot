package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.AccessControlService;
import com.chicu.neurotradebot.trading.service.TradingSessionService;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class TradeCallbackHandler {

    private final TradingSessionService tradingSessionService;
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

        String resultText;
        try {
            tradingSessionService.startSession(chatId);
            resultText = "✅ Торговая сессия успешно запущена на Binance!";
        } catch (Exception e) {
            resultText = "❌ Ошибка запуска торговой сессии: " + e.getMessage();
        }

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(resultText)
                .replyMarkup(startMenuBuilder.buildMainMenu())
                .build();
    }
}
