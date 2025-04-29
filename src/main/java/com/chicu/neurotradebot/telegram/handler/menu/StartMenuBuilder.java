package com.chicu.neurotradebot.telegram.handler.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartMenuBuilder {

    public InlineKeyboardMarkup buildMainMenu() {
        InlineKeyboardButton tradeButton = InlineKeyboardButton.builder()
                .text("🚀 Торговля")
                .callbackData("trade_menu")
                .build();

        InlineKeyboardButton settingsButton = InlineKeyboardButton.builder()
                .text("⚙️ Настройки")
                .callbackData("settings_menu")
                .build();

        InlineKeyboardButton analyticsButton = InlineKeyboardButton.builder()
                .text("📈 Аналитика")
                .callbackData("analytics_menu")
                .build();

        InlineKeyboardButton subscriptionButton = InlineKeyboardButton.builder()
                .text("💳 Подписка")
                .callbackData("subscribe_menu")
                .build();

        InlineKeyboardButton helpButton = InlineKeyboardButton.builder()
                .text("🛠️ Помощь")
                .callbackData("help_menu")
                .build();

        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(tradeButton),
                List.of(settingsButton),
                List.of(analyticsButton),
                List.of(subscriptionButton),
                List.of(helpButton)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
