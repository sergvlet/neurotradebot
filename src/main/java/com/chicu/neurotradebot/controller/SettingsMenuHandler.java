package com.chicu.neurotradebot.controller;

import com.chicu.neurotradebot.model.ConnectionLog;
import com.chicu.neurotradebot.model.ExchangeCredential;
import com.chicu.neurotradebot.model.TradeMode;
import com.chicu.neurotradebot.model.User;
import com.chicu.neurotradebot.model.UserTradingSettings;
import com.chicu.neurotradebot.repository.ConnectionLogRepository;
import com.chicu.neurotradebot.repository.ExchangeCredentialRepository;
import com.chicu.neurotradebot.repository.UserRepository;
import com.chicu.neurotradebot.repository.UserTradingSettingsRepository;
import com.chicu.neurotradebot.service.ExchangeConnectionService;
import com.chicu.neurotradebot.session.UserSessionManager;
import com.chicu.neurotradebot.view.SettingsMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class SettingsMenuHandler {

    private final SettingsMenuBuilder settingsMenuBuilder;
    private final ExchangeCredentialRepository credentialRepository;
    private final ExchangeConnectionService exchangeConnectionService;
    private final AITradeMenuHandler aiTradeMenuHandler;
    private final UserRepository userRepository;
    private final UserTradingSettingsRepository tradingSettingsRepository;
    private final ConnectionLogRepository connectionLogRepository;

    public Object handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();
        String userActionNote = "";

        if ("switch_mode".equals(data)) {
            boolean newTestnet = !UserSessionManager.isTestnet(chatId);
            UserSessionManager.setTestnet(chatId, newTestnet);

            userRepository.findById(chatId).ifPresent(user -> {
                UserTradingSettings settings = tradingSettingsRepository.findByUserId(chatId).orElseGet(() -> {
                    UserTradingSettings s = new UserTradingSettings();
                    s.setUser(user);
                    s.setTradeMode(TradeMode.MANUAL);
                    return s;
                });
                settings.setUseTestnet(newTestnet);
                tradingSettingsRepository.save(settings);
            });

            userActionNote = "🔁 Сеть переключена на *" + (newTestnet ? "Test (тестовая)" : "Real (реальная)") + "*";
        }

        if ("select_manual_mode".equals(data)) {
            saveTradeMode(chatId, TradeMode.MANUAL);
            userActionNote = "✅ Выбран режим торговли: *Manual*";
            return checkConnectionAndProceed(update, chatId, userActionNote, true);
        }

        if ("select_ai_mode".equals(data)) {
            saveTradeMode(chatId, TradeMode.AI);
            userActionNote = "✅ Выбран режим торговли: *AI*";
            return checkConnectionAndProceed(update, chatId, userActionNote, true);
        }

        // Восстановление данных из базы
        tradingSettingsRepository.findByUserId(chatId).ifPresent(settings -> {
            if (settings.getTradeMode() != null)
                UserSessionManager.setTradeMode(chatId, settings.getTradeMode());
            if (settings.getUseTestnet() != null)
                UserSessionManager.setTestnet(chatId, settings.getUseTestnet());
            if (settings.getSelectedExchange() != null)
                UserSessionManager.setSelectedExchange(chatId, settings.getSelectedExchange());
        });

        return checkConnectionAndProceed(update, chatId, userActionNote, false);
    }

    private Object checkConnectionAndProceed(Update update, Long chatId, String userActionNote, boolean allowRedirectToAiMenu) {
        String exchange = UserSessionManager.getSelectedExchange(chatId);
        boolean isTestnet = UserSessionManager.isTestnet(chatId);

        if (exchange == null || exchange.isEmpty() || "Не выбрана".equals(exchange)) {
            return errorMessage(update, "⚠️ Ошибка: биржа не выбрана.\nПожалуйста, выберите биржу в настройках торговли.");
        }

        var credentialOpt = credentialRepository.findByUserIdAndExchange(chatId, exchange);
        if (credentialOpt.isEmpty()) {
            return errorMessage(update, "⚠️ Ошибка: API-ключи не найдены.\nПожалуйста, настройте их через меню настроек.");
        }

        var credential = credentialOpt.get();
        boolean keysExist = isTestnet
                ? credential.getTestApiKey() != null && credential.getTestSecretKey() != null
                : credential.getRealApiKey() != null && credential.getRealSecretKey() != null;

        boolean connectionSuccessful = false;
        String apiStatus = keysExist ? "✅ Найдены" : "❌ Не найдены";
        String connectionStatus = "⛔️ Не проверено";

        if (keysExist) {
            connectionSuccessful = exchangeConnectionService.testConnection(
                    exchange,
                    isTestnet ? credential.getTestApiKey() : credential.getRealApiKey(),
                    isTestnet ? credential.getTestSecretKey() : credential.getRealSecretKey(),
                    isTestnet
            );
            connectionStatus = connectionSuccessful ? "✅ Установлено" : "❌ Ошибка";
        }

        // Логируем
        ConnectionLog log = new ConnectionLog();
        log.setUser(userRepository.findById(chatId).orElse(null));
        log.setExchange(exchange);
        log.setTestnet(isTestnet);
        log.setSuccess(keysExist && connectionSuccessful);
        log.setMessage(keysExist ? (connectionSuccessful ? "Соединение успешно" : "Ошибка подключения") : "API-ключи отсутствуют");
        connectionLogRepository.save(log);

        String network = isTestnet ? "Test (тестовая сеть)" : "Real (реальная сеть)";
        String tradeMode = UserSessionManager.getTradeMode(chatId).name();

        String summary = String.format("""
                %s

                ⚙️ *Текущие настройки торговли:*

                Сеть: *%s*
                Биржа: *%s*
                API-ключи: %s
                Соединение: %s
                Режим торговли: *%s*

                Выберите действие:
                """, userActionNote != null ? userActionNote : "", network, exchange, apiStatus, connectionStatus, tradeMode);

        if (UserSessionManager.getTradeMode(chatId) == TradeMode.AI && allowRedirectToAiMenu) {
            return aiTradeMenuHandler.showMainMenu(update);
        } else {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text(summary)
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }
    }

    private EditMessageText errorMessage(Update update, String text) {
        return EditMessageText.builder()
                .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .text(text)
                .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                .parseMode("Markdown")
                .build();
    }

    private void saveTradeMode(Long chatId, TradeMode mode) {
        User user = userRepository.findById(chatId).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(chatId);
            return userRepository.save(newUser);
        });

        UserTradingSettings settings = tradingSettingsRepository.findByUserId(chatId).orElseGet(() -> {
            UserTradingSettings s = new UserTradingSettings();
            s.setUser(user);
            s.setTradeMode(TradeMode.MANUAL);
            return s;
        });

        settings.setTradeMode(mode);
        tradingSettingsRepository.save(settings);

        UserSessionManager.setTradeMode(chatId, mode);
    }
}