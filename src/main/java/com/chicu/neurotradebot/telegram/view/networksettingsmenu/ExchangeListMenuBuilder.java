// src/main/java/com/chicu/neurotradebot/telegram/handler/ExchangeListMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.networksettingsmenu;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

@Component
public class ExchangeListMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        // ключ, с которым мы отрисовываем этот список
        return Set.of("select_exchange");
    }

    @Override
    public String title() {
        return "🌐 Выберите биржу:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // перечислите здесь все биржи, которые хотите поддержать
        InlineKeyboardButton binance = InlineKeyboardButton.builder()
            .text("Binance")
            .callbackData("exchange:binance")
            .build();

        InlineKeyboardButton ftx = InlineKeyboardButton.builder()
            .text("FTX")
            .callbackData("exchange:ftx")
            .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("🔙 Назад")
            .callbackData("back_to_settings")
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(binance),
                List.of(ftx),
                List.of(back)
            ))
            .build();
    }
}
