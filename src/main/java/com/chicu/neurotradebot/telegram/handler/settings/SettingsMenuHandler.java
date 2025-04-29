package com.chicu.neurotradebot.telegram.handler.settings;

import com.chicu.neurotradebot.telegram.handler.exchange.common.ExchangeConnectionService;
import com.chicu.neurotradebot.telegram.handler.menu.SettingsMenuBuilder;
import com.chicu.neurotradebot.telegram.handler.trade.ai.AITradeMenuHandler;
import com.chicu.neurotradebot.telegram.session.TradeMode;
import com.chicu.neurotradebot.telegram.session.UserSessionManager;
import com.chicu.neurotradebot.user.repository.ExchangeCredentialRepository;
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

    public Object handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String data = update.getCallbackQuery().getData();

        if ("switch_mode".equals(data)) {
            UserSessionManager.toggleTestnet(chatId);
        }

        if ("select_manual_mode".equals(data)) {
            UserSessionManager.setTradeMode(chatId, TradeMode.MANUAL);
            return checkConnectionAndProceed(update, chatId);
        }

        if ("select_ai_mode".equals(data)) {
            UserSessionManager.setTradeMode(chatId, TradeMode.AI);
            return checkConnectionAndProceed(update, chatId);
        }

        boolean isTestnet = UserSessionManager.isTestnet(chatId);
        String network = isTestnet ? "Test (тестовая сеть)" : "Real (реальная сеть)";
        String exchange = UserSessionManager.getSelectedExchange(chatId);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(String.format("""
                        ⚙️ *Настройки торговли:*

                        Сеть: *%s*
                        Биржа: *%s*

                        Выберите действие:
                        """, network, exchange))
                .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                .parseMode("Markdown")
                .build();
    }

    private Object checkConnectionAndProceed(Update update, Long chatId) {
        String exchange = UserSessionManager.getSelectedExchange(chatId);
        boolean useTestnet = UserSessionManager.isTestnet(chatId);

        if (exchange == null || exchange.isEmpty() || "Не выбрана".equals(exchange)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("⚠️ Ошибка: биржа не выбрана.\nПожалуйста, выберите биржу в настройках торговли.")
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        var credentialOpt = credentialRepository.findByUserIdAndExchange(chatId, exchange);
        if (credentialOpt.isEmpty()) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("⚠️ Ошибка: API-ключи не найдены.\nПожалуйста, настройте их через меню настроек.")
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        var credential = credentialOpt.get();
        boolean keysExist = useTestnet
                ? credential.getTestApiKey() != null && credential.getTestSecretKey() != null
                : credential.getRealApiKey() != null && credential.getRealSecretKey() != null;

        if (!keysExist) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("⚠️ Ошибка: нет ключей для выбранной сети.\nПожалуйста, настройте их через меню настроек.")
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        boolean connectionSuccessful = exchangeConnectionService.testConnection(
                exchange,
                useTestnet ? credential.getTestApiKey() : credential.getRealApiKey(),
                useTestnet ? credential.getTestSecretKey() : credential.getRealSecretKey(),
                useTestnet
        );

        if (!connectionSuccessful) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("⚠️ Ошибка соединения с биржей.\nПроверьте ваши API-ключи.")
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        TradeMode mode = UserSessionManager.getTradeMode(chatId);

        if (mode == TradeMode.MANUAL) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text("🧑‍💻 *Режим торговли выбран: Ручная торговля*.\n\nНастройки будут доступны в следующем меню.")
                    .parseMode("Markdown")
                    .build();
        } else {
            return aiTradeMenuHandler.showMainMenu(update);
        }
    }
}
