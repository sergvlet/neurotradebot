package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.user.service.ApiKeySetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ExchangeSelectHandler {

    private final ApiKeySetupService apiKeySetupService;

    public EditMessageText handleEdit(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callbackData = update.getCallbackQuery().getData();

        String selectedExchange = switch (callbackData) {
            case "EXCHANGE_BINANCE" -> "BINANCE";
            case "EXCHANGE_BYBIT" -> "BYBIT";
            case "EXCHANGE_KUCOIN" -> "KUCOIN";
            default -> throw new RuntimeException("Неизвестная биржа: " + callbackData);
        };

        apiKeySetupService.setSelectedExchange(chatId, selectedExchange);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .text("""
                      🛡️ Отлично! Теперь отправьте ваш *API-ключ* для биржи:
                      """)
                .parseMode("Markdown")
                .build();
    }
}
