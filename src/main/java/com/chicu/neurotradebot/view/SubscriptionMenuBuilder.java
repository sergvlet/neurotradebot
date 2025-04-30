package com.chicu.neurotradebot.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionMenuBuilder {

    public InlineKeyboardMarkup buildSubscriptionMenu() {
        List<List<InlineKeyboardButton>> keyboard = List.of(
                List.of(
                        InlineKeyboardButton.builder()
                                .text("🚀 Бесплатный триал 10 дней")
                                .callbackData("subscribe_trial")
                                .build()
                ),
                List.of(
                        InlineKeyboardButton.builder()
                                .text("📅 1 месяц")
                                .callbackData("subscribe_1m")
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("📅 3 месяца")
                                .callbackData("subscribe_3m")
                                .build()
                ),
                List.of(
                        InlineKeyboardButton.builder()
                                .text("📅 6 месяцев")
                                .callbackData("subscribe_6m")
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("📅 12 месяцев")
                                .callbackData("subscribe_1y")
                                .build()
                ),
                List.of(
                        InlineKeyboardButton.builder()
                                .text("🔙 Назад")
                                .callbackData("back_to_main")
                                .build()
                )
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
