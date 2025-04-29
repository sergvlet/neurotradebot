package com.chicu.neurotradebot.telegram.handler.api;

import com.chicu.neurotradebot.telegram.handler.exchange.entity.ExchangeCredential;
import com.chicu.neurotradebot.telegram.handler.menu.SettingsMenuBuilder;
import com.chicu.neurotradebot.telegram.session.InputStage;
import com.chicu.neurotradebot.telegram.session.UserSessionManager;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.repository.ExchangeCredentialRepository;
import com.chicu.neurotradebot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeySetupHandler {

    private final ExchangeCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final SettingsMenuBuilder settingsMenuBuilder;

    public Object handle(Update update) {
        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            String data = update.getCallbackQuery().getData();

            if ("api_setup_start".equals(data)) {
                return checkExistingKeys(chatId, messageId);
            }
            if ("api_replace_confirm".equals(data)) {
                UserSessionManager.setInputStage(chatId, InputStage.ENTER_API_KEY);
                return EditMessageText.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(messageId)
                        .text("✏️ Пожалуйста, отправьте ваш *новый API-ключ*:")
                        .parseMode("Markdown")
                        .build();
            }
            if ("api_replace_cancel".equals(data)) {
                UserSessionManager.setInputStage(chatId, InputStage.NONE);
                return EditMessageText.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(messageId)
                        .text("""
                              ❌ Оставляем старые ключи.

                              ⚙️ Возвращаю вас в меню настроек:
                              """)
                        .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                        .parseMode("Markdown")
                        .build();
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            return handleMessage(update.getMessage());
        }

        return null;
    }

    public Object handleMessage(Message message) {
        String text = message.getText();
        long chatId = message.getChatId();

        InputStage stage = UserSessionManager.getInputStage(chatId);

        if (stage == InputStage.ENTER_API_KEY) {
            UserSessionManager.saveTempApiKey(chatId, text);
            UserSessionManager.setInputStage(chatId, InputStage.ENTER_SECRET_KEY);

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("✏️ Теперь отправьте *Secret-ключ*:")
                    .parseMode("Markdown")
                    .build();
        } else if (stage == InputStage.ENTER_SECRET_KEY) {
            String apiKey = UserSessionManager.getTempApiKey(chatId);
            String exchange = UserSessionManager.getSelectedExchange(chatId);
            boolean useTestnet = UserSessionManager.isTestnet(chatId);

            if (exchange == null || exchange.isEmpty() || "Не выбрана".equals(exchange)) {
                UserSessionManager.setInputStage(chatId, InputStage.NONE);
                UserSessionManager.clearTempApiKey(chatId);

                return SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("⚠️ Ошибка: биржа не выбрана. Сначала выберите биржу в настройках торговли!")
                        .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                        .parseMode("Markdown")
                        .build();
            }

            User user = userRepository.findById(chatId)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(chatId);
                        newUser.setCreatedAt(LocalDateTime.now());
                        return userRepository.save(newUser);
                    });

            ExchangeCredential credential = credentialRepository.findByUserIdAndExchange(chatId, exchange)
                    .orElseGet(() -> {
                        ExchangeCredential newCredential = new ExchangeCredential();
                        newCredential.setCreatedAt(LocalDateTime.now());
                        return newCredential;
                    });

            credential.setUser(user);
            credential.setExchange(exchange);
            credential.setUseTestnet(useTestnet);

            if (useTestnet) {
                credential.setTestApiKey(apiKey);
                credential.setTestSecretKey(text);
            } else {
                credential.setRealApiKey(apiKey);
                credential.setRealSecretKey(text);
            }

            credentialRepository.save(credential);

            UserSessionManager.setInputStage(chatId, InputStage.NONE);
            UserSessionManager.clearTempApiKey(chatId);

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("""
                          ✅ Ваши API-ключи успешно сохранены!

                          ⚙️ Возвращаю вас в меню настроек:
                          """)
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        return null;
    }

    private Object checkExistingKeys(long chatId, Integer messageId) {
        String exchange = UserSessionManager.getSelectedExchange(chatId);
        boolean useTestnet = UserSessionManager.isTestnet(chatId);

        if (exchange == null || exchange.isEmpty() || "Не выбрана".equals(exchange)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("⚠️ Ошибка: биржа не выбрана. Сначала выберите биржу в настройках торговли!")
                    .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }

        var credentialOpt = credentialRepository.findByUserIdAndExchange(chatId, exchange);

        if (credentialOpt.isPresent()) {
            var credential = credentialOpt.get();

            boolean keysExist = useTestnet
                    ? credential.getTestApiKey() != null && credential.getTestSecretKey() != null
                    : credential.getRealApiKey() != null && credential.getRealSecretKey() != null;

            if (keysExist) {
                return EditMessageText.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(messageId)
                        .text("""
                          ⚠️ Ключи для выбранной сети уже существуют.

                          Хотите заменить их новыми?
                          """)
                        .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboard(List.of(
                                        List.of(
                                                InlineKeyboardButton.builder()
                                                        .text("✅ Заменить ключи")
                                                        .callbackData("api_replace_confirm")
                                                        .build(),
                                                InlineKeyboardButton.builder()
                                                        .text("❌ Оставить старые")
                                                        .callbackData("api_replace_cancel")
                                                        .build()
                                        )
                                ))
                                .build())
                        .parseMode("Markdown")
                        .build();
            }
        }

        UserSessionManager.setInputStage(chatId, InputStage.ENTER_API_KEY);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("✏️ Пожалуйста, отправьте ваш *API-ключ*:")
                .parseMode("Markdown")
                .build();
    }
}
