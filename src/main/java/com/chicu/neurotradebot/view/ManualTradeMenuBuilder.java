// src/main/java/com/chicu/neurotradebot/view/ManualTradeMenuBuilder.java
package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class ManualTradeMenuBuilder {

    public InlineKeyboardMarkup buildManualTradeMenu() {
        InlineKeyboardButton placeOrder = InlineKeyboardButton.builder()
                .text("💵 Создать ордер")
                .callbackData("manual_place_order")
                .build();

        InlineKeyboardButton activeOrders = InlineKeyboardButton.builder()
                .text("📋 Активные ордера")
                .callbackData("manual_active_orders")
                .build();

        InlineKeyboardButton cancelOrder = InlineKeyboardButton.builder()
                .text("❌ Отменить ордер")
                .callbackData("manual_cancel_order")
                .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("back_to_main")
                .build();

        List<List<InlineKeyboardButton>> rows = List.of(
            List.of(placeOrder),
            List.of(activeOrders),
            List.of(cancelOrder),
            List.of(back)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
