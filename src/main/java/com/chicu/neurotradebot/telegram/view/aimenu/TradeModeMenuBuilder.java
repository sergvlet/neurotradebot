// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/TradeModeMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Set;

@Component
public class TradeModeMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_trade_mode");
    }

    @Override
    public String title() {
        return "⚙️ Выберите режим торговли";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // Ключ для возврата в главное AI-меню
        String backCallback = "apply_network_settings_ai";

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("SPOT 📈")
                                        .callbackData("mode_SPOT")
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text("MARGIN ⚖️")
                                        .callbackData("mode_MARGIN")
                                        .build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("FUTURES USDT 💵")
                                        .callbackData("mode_FUTURES_USDT")
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text("FUTURES COIN 🪙")
                                        .callbackData("mode_FUTURES_COIN")
                                        .build()
                        ),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("⬅️ Назад")
                                        .callbackData(backCallback)
                                        .build()
                        )
                ))
                .build();
    }
}
