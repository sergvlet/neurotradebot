package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class TradeLimitInputProcessor {

    private final UserSettingsService userSettingsService;
    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    /**
     * Обработка текстового ввода лимита
     */
    public void process(Message message, AbsSender sender) {
        Long chatId = message.getChatId();
        String text = message.getText();

        if (!userSettingsService.isWaitingFor(chatId, "TRADE_LIMIT")) return;

        try {
            double limit = Double.parseDouble(text.replace(",", "."));
            if (limit <= 0) throw new NumberFormatException();

            userSettingsService.setTradeLimit(chatId, limit);
            userSettingsService.clearWaiting(chatId);

            String response = "✅ Лимит сделки установлен: " + limit + " USDT";
            InlineKeyboardMarkup keyboard = keyboardService.appendBackButton(new ArrayList<>());

            messageUtils.sendMessage(chatId, response, keyboard, sender);
        } catch (NumberFormatException e) {
            messageUtils.sendMessage(chatId, "❌ Введите корректное число (например: 50 или 25.5)", sender);
        }
    }
}
