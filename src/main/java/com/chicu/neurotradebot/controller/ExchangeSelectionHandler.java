package com.chicu.neurotradebot.controller;

import com.chicu.neurotradebot.model.ExchangeCredential;
import com.chicu.neurotradebot.model.TradeMode;
import com.chicu.neurotradebot.model.User;
import com.chicu.neurotradebot.model.UserTradingSettings;
import com.chicu.neurotradebot.repository.ExchangeCredentialRepository;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.repository.UserTradingSettingsRepository;
import com.chicu.neurotradebot.session.UserSessionManager;
import com.chicu.neurotradebot.view.ExchangeMenuBuilder;
import com.chicu.neurotradebot.view.SettingsMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExchangeSelectionHandler {

    private final ExchangeMenuBuilder exchangeMenuBuilder;
    private final SettingsMenuBuilder settingsMenuBuilder;
    private final UserRepository userRepository;
    private final UserTradingSettingsRepository tradingSettingsRepository;
    private final ExchangeCredentialRepository credentialRepository;

    public Object handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String data = update.getCallbackQuery().getData();

        if ("select_exchange".equals(data)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("🌐 *Выберите биржу для торговли:*")
                    .replyMarkup(exchangeMenuBuilder.buildExchangeSelectionMenu())
                    .parseMode("Markdown")
                    .build();
        }

        if (data.startsWith("select_")) {
            String exchangeKey = data.replace("select_", "");
            String exchangeName = switch (exchangeKey) {
                case "binance" -> "Binance";
                case "bybit" -> "Bybit";
                case "okx" -> "OKX";
                default -> "Не выбрана";
            };

            UserSessionManager.setSelectedExchange(chatId, exchangeName);

            User user = userRepository.findById(chatId).orElseGet(() -> {
                User newUser = new User();
                newUser.setId(chatId);
                newUser.setCreatedAt(LocalDateTime.now());
                return userRepository.save(newUser);
            });

            ExchangeCredential credential = credentialRepository.findByUserIdAndExchange(chatId, exchangeName)
                    .orElseGet(() -> {
                        ExchangeCredential newCredential = new ExchangeCredential();
                        newCredential.setUser(user);
                        newCredential.setExchange(exchangeName);
                        newCredential.setCreatedAt(LocalDateTime.now());
                        return newCredential;
                    });

            boolean isTestnet = UserSessionManager.isTestnet(chatId);
            credential.setUseTestnet(isTestnet);
            credential.setExchange(exchangeName);
            credential.setUser(user);
            credentialRepository.save(credential);

            UserTradingSettings settings = tradingSettingsRepository.findByUserId(chatId).orElseGet(() -> {
                UserTradingSettings s = new UserTradingSettings();
                s.setUser(user);
                s.setTradeMode(TradeMode.MANUAL);
                return s;
            });

            settings.setSelectedExchange(exchangeName);
            tradingSettingsRepository.save(settings);

            String network = isTestnet ? "Test (тестовая сеть)" : "Real (реальная сеть)";
            String userActionNote = "✅ Вы выбрали биржу: *" + exchangeName + "*";

            String summary = String.format("""
                    %s

                    ⚙️ *Текущие настройки торговли:*

                    Сеть: *%s*
                    Биржа: *%s*

                    Выберите действие:
                    """, userActionNote, network, exchangeName);

            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text(summary)
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        return null;
    }
}