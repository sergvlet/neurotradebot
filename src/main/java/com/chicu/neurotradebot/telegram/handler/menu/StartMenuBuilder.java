package com.chicu.neurotradebot.telegram.handler.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class StartMenuBuilder {

    public InlineKeyboardMarkup buildMainMenu() {
        InlineKeyboardButton balanceButton = InlineKeyboardButton.builder()
                .text("📊 Мой баланс")
                .callbackData("BALANCE")
                .build();

        InlineKeyboardButton tradeButton = InlineKeyboardButton.builder()
                .text("📈 Торговля")
                .callbackData("TRADE")
                .build();

        InlineKeyboardButton tradingModeButton = InlineKeyboardButton.builder()
                .text("🎯 Режим торговли")
                .callbackData("TRADING_MODE")
                .build();

        InlineKeyboardButton settingsButton = InlineKeyboardButton.builder()
                .text("⚙️ Настройки")
                .callbackData("SETTINGS")
                .build();

        InlineKeyboardButton statsButton = InlineKeyboardButton.builder()
                .text("📉 Статистика")
                .callbackData("STATS")
                .build();

        InlineKeyboardButton subscriptionButton = InlineKeyboardButton.builder()
                .text("👤 Подписка")
                .callbackData("SUBSCRIPTION")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(balanceButton),
                        List.of(tradeButton),
                        List.of(tradingModeButton),
                        List.of(settingsButton),
                        List.of(statsButton),
                        List.of(subscriptionButton)
                ))
                .build();
    }

    // 🔥 Добавляем НОВЫЙ метод сюда:
    public InlineKeyboardMarkup buildExchangeSelectionKeyboard() {
        InlineKeyboardButton binanceButton = InlineKeyboardButton.builder()
                .text("Binance")
                .callbackData("EXCHANGE_BINANCE")
                .build();

        InlineKeyboardButton bybitButton = InlineKeyboardButton.builder()
                .text("Bybit")
                .callbackData("EXCHANGE_BYBIT")
                .build();

        InlineKeyboardButton kucoinButton = InlineKeyboardButton.builder()
                .text("KuCoin")
                .callbackData("EXCHANGE_KUCOIN")
                .build();

        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("MAIN_MENU")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(binanceButton),
                        List.of(bybitButton),
                        List.of(kucoinButton),
                        List.of(backButton)
                ))
                .build();
    }
    // ДОБАВИТЬ вместе с остальными методами:
    public InlineKeyboardMarkup buildSettingsMenu() {
        InlineKeyboardButton tradingModeButton = InlineKeyboardButton.builder()
                .text("🔹 Переключить режим торговли (Тест/Реал)")
                .callbackData("TOGGLE_TRADING_MODE")
                .build();

        InlineKeyboardButton setupApiKeysButton = InlineKeyboardButton.builder()
                .text("🔹 Настроить API-ключи")
                .callbackData("SETUP_API_KEYS")
                .build();

        InlineKeyboardButton chooseExchangeButton = InlineKeyboardButton.builder()
                .text("🔹 Выбрать биржу")
                .callbackData("SETUP_API_KEYS") // пока используем ту же кнопку для выбора биржи
                .build();

        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("🔙 Назад в меню")
                .callbackData("MAIN_MENU")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(tradingModeButton),
                        List.of(setupApiKeysButton),
                        List.of(chooseExchangeButton),
                        List.of(backButton)
                ))
                .build();
    }

}
