package com.chicu.neurotradebot.controller;

import com.chicu.neurotradebot.view.ExchangeMenuBuilder;
import com.chicu.neurotradebot.view.SettingsMenuBuilder;
import com.chicu.neurotradebot.session.UserSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ExchangeSelectionHandler {

    private final ExchangeMenuBuilder exchangeMenuBuilder;
    private final SettingsMenuBuilder settingsMenuBuilder;

    public Object handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String data = update.getCallbackQuery().getData();

        if ("select_exchange".equals(data)) {
            // Нажали кнопку "Выбрать биржу" — показываем меню выбора биржи
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                        🌐 *Выберите биржу для торговли:*
                        """)
                    .replyMarkup(exchangeMenuBuilder.buildExchangeSelectionMenu())
                    .parseMode("Markdown")
                    .build();
        }

        if (data.startsWith("select_")) {
            // Нажали одну из бирж (binance/bybit/okx)
            String exchangeKey = data.replace("select_", "");
            String exchangeName;

            switch (exchangeKey) {
                case "binance" -> exchangeName = "Binance";
                case "bybit" -> exchangeName = "Bybit";
                case "okx" -> exchangeName = "OKX";
                default -> exchangeName = "Не выбрана";
            }

            UserSessionManager.setSelectedExchange(chatId, exchangeName);

            // После выбора возвращаем в настройки торговли
            boolean isTestnet = UserSessionManager.isTestnet(chatId);
            String network = isTestnet ? "Test (тестовая сеть)" : "Real (реальная сеть)";
            String selectedExchange = UserSessionManager.getSelectedExchange(chatId);

            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text(String.format("""
                        ⚙️ *Настройки торговли:*

                        Сеть: *%s*
                        Биржа: *%s*

                        Выберите действие:
                        """, network, selectedExchange))
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        return null;
    }
}
