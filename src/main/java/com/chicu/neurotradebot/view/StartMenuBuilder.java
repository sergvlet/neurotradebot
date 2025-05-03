package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class StartMenuBuilder {

    public InlineKeyboardMarkup buildMainMenu() {
        InlineKeyboardButton aboutBotButton = InlineKeyboardButton.builder()
                .text("ℹ️ О боте")
                .callbackData("about_bot")
                .build();

        InlineKeyboardButton subscriptionButton = InlineKeyboardButton.builder()
                .text("💳 Подписка")
                .callbackData("subscribe_menu")
                .build();

        InlineKeyboardButton languageButton = InlineKeyboardButton.builder()
                .text("🌐 Выбор языка")
                .callbackData("language_menu")
                .build();

        InlineKeyboardButton manualTradeButton = InlineKeyboardButton.builder()
                .text("🛠️ Ручная торговля")
                .callbackData("select_manual_mode")       // ← изменено
                .build();

        InlineKeyboardButton aiTradeButton = InlineKeyboardButton.builder()
                .text("🤖 AI Торговля")
                .callbackData("select_ai_mode")           // ← изменено
                .build();

        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(aboutBotButton),
                List.of(subscriptionButton),
                List.of(languageButton),
                List.of(manualTradeButton),
                List.of(aiTradeButton)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
