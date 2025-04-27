package com.chicu.neurotradebot.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SelectCoinHandler {

    public EditMessageText handle(long chatId, Integer messageId) {
        InlineKeyboardButton btcButton = InlineKeyboardButton.builder()
                .text("🪙 BTC")
                .callbackData("SELECT_COIN_BTC")
                .build();

        InlineKeyboardButton ethButton = InlineKeyboardButton.builder()
                .text("🪙 ETH")
                .callbackData("SELECT_COIN_ETH")
                .build();

        InlineKeyboardButton bnbButton = InlineKeyboardButton.builder()
                .text("🪙 BNB")
                .callbackData("SELECT_COIN_BNB")
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(btcButton),
                        List.of(ethButton),
                        List.of(bnbButton)
                ))
                .build();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                      📋 Выберите монету для покупки:
                      """)
                .replyMarkup(markup)
                .build();
    }
}
