package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.exchange.binance.service.BinanceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BuyCoinHandler {

    private final BinanceOrderService binanceOrderService;

    public EditMessageText handleBuyCoin(long chatId, Integer messageId, String symbol) {
        try {
            // Пока фиксированное количество для теста
            BigDecimal quantity = new BigDecimal("0.001");

            Long userId = chatId; // В реальной версии будет другой userId

            String result = binanceOrderService.buy(symbol, quantity, userId);

            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("✅ Ордер на покупку " + symbol + " отправлен!\n\nОтвет биржи:\n" + result)
                    .build();
        } catch (Exception e) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("❌ Ошибка при покупке монеты: " + e.getMessage())
                    .build();
        }
    }
}
