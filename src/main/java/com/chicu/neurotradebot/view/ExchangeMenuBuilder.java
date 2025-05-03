// src/main/java/com/chicu/neurotradebot/view/ExchangeMenuBuilder.java
package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * Строит меню выбора биржи.
 */
@Component
public class ExchangeMenuBuilder {

    /**
     * Показывает список поддерживаемых бирж.
     * При необходимости расширьте новыми пунктами.
     */
    public InlineKeyboardMarkup buildExchangeSelectionMenu() {
        InlineKeyboardButton binance = InlineKeyboardButton.builder()
                .text("🏦 Binance")
                .callbackData("exchange_binance")
                .build();

        InlineKeyboardButton coinbase = InlineKeyboardButton.builder()
                .text("💰 Coinbase")
                .callbackData("exchange_coinbase")
                .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("back_to_main")
                .build();

        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(binance),
                List.of(coinbase),
                List.of(back)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
