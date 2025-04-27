package com.chicu.neurotradebot.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMethodSelectHandler {

    public EditMessageText handle(long chatId, Integer messageId) {
        InlineKeyboardButton trc20Button = InlineKeyboardButton.builder()
                .text("💳 USDT (TRC20)")
                .callbackData("PAY_TRX")
                .build();

        InlineKeyboardButton erc20Button = InlineKeyboardButton.builder()
                .text("💳 USDT (ERC20)")
                .callbackData("PAY_ETH")
                .build();

        InlineKeyboardButton btcButton = InlineKeyboardButton.builder()
                .text("💳 Bitcoin (BTC)")
                .callbackData("PAY_BTC")
                .build();

        InlineKeyboardButton confirmPaymentButton = InlineKeyboardButton.builder()
                .text("✅ Я оплатил")
                .callbackData("CONFIRM_PAYMENT")
                .build(); // ✅ ДОБАВЛЯЕМ эту кнопку

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(trc20Button),
                        List.of(erc20Button),
                        List.of(btcButton),
                        List.of(confirmPaymentButton) // ✅ новая строка с кнопкой
                ))
                .build();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                      💵 Выберите способ оплаты:
                      
                      После перевода нажмите кнопку "✅ Я оплатил".
                      """)
                .replyMarkup(markup)
                .build();
    }
}
