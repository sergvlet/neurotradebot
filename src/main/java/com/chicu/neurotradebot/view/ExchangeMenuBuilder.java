package com.chicu.neurotradebot.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExchangeMenuBuilder {

    public InlineKeyboardMarkup buildExchangeSelectionMenu() {
        InlineKeyboardButton binanceButton = InlineKeyboardButton.builder()
                .text("Binance")
                .callbackData("select_binance")
                .build();

        InlineKeyboardButton bybitButton = InlineKeyboardButton.builder()
                .text("Bybit")
                .callbackData("select_bybit")
                .build();

        InlineKeyboardButton okxButton = InlineKeyboardButton.builder()
                .text("OKX")
                .callbackData("select_okx")
                .build();

        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("back_to_settings")
                .build();

        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(binanceButton),
                List.of(bybitButton),
                List.of(okxButton),
                List.of(backButton)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
