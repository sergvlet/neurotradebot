package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.exchange.core.ExchangeClient;
import com.chicu.neurotradebot.exchange.core.ExchangeRegistry;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import com.chicu.neurotradebot.user.entity.ExchangeCredential;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.enums.ApiKeySetupStage;
import com.chicu.neurotradebot.user.repository.ExchangeCredentialRepository;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.user.service.ApiKeySetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ApiKeySetupHandler {

    private final ApiKeySetupService apiKeySetupService;
    private final ExchangeCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final ExchangeRegistry exchangeRegistry;
    private final StartMenuBuilder menuBuilder;

    // Начало настройки API ключей — выбор биржи
    public SendMessage handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        apiKeySetupService.startSetup(chatId);

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("""
                      🛡️ Настройка ключей API.

                      Выберите биржу для которой хотите добавить ключи:
                      """)
                .replyMarkup(menuBuilder.buildExchangeSelectionKeyboard())
                .parseMode("Markdown")
                .build();
    }



    // Обработка ввода текста (API ключ или Секретный ключ)
    public SendMessage handleApiKeyInput(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText();

        var stage = apiKeySetupService.getUserStage(chatId);

        if (stage == ApiKeySetupStage.ENTER_API_KEY) {
            apiKeySetupService.saveTempApiKey(chatId, text);
            apiKeySetupService.setUserStage(chatId, ApiKeySetupStage.ENTER_SECRET_KEY);

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("✏️ Теперь отправьте *секретный ключ* API:")
                    .parseMode("Markdown")
                    .build();

        } else if (stage == ApiKeySetupStage.ENTER_SECRET_KEY) {
            return saveKeysAndCheckConnection(chatId, text, message.getMessageId());
        } else {
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("⚠️ Ошибка: неверная стадия настройки. Попробуйте начать заново.")
                    .parseMode("Markdown")
                    .build();
        }
    }

    // Логика сохранения ключей и проверки подключения
    private SendMessage saveKeysAndCheckConnection(long chatId, String secretKey, Integer messageId) {
        String apiKey = apiKeySetupService.getTempApiKey(chatId);
        String selectedExchange = apiKeySetupService.getSelectedExchange(chatId);
        boolean useTestnet = apiKeySetupService.isUseTestnet(chatId);

        if (apiKey == null || selectedExchange == null) {
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("⚠️ Ошибка данных. Попробуйте заново.")
                    .parseMode("Markdown")
                    .build();
        }

        User user = userRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

        ExchangeCredential credential = credentialRepository.findByUserIdAndExchange(chatId, selectedExchange)
                .orElseGet(() -> {
                    ExchangeCredential newCredential = new ExchangeCredential();
                    newCredential.setUser(user);
                    newCredential.setExchange(selectedExchange);
                    newCredential.setCreatedAt(LocalDateTime.now());
                    return newCredential;
                });

        credential.setUseTestnet(useTestnet);

        if (useTestnet) {
            credential.setTestApiKey(apiKey);
            credential.setTestSecretKey(secretKey);
        } else {
            credential.setRealApiKey(apiKey);
            credential.setRealSecretKey(secretKey);
        }

        credentialRepository.save(credential);

        // Проверка подключения
        try {
            ExchangeClient client = exchangeRegistry.getClient(selectedExchange);
            boolean isConnected = client.testConnection(chatId);

            apiKeySetupService.completeSetup(chatId);

            if (!isConnected) {
                return SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("""
                              ❌ Подключение к бирже не удалось.

                              Проверьте правильность введённых ключей.
                              """)
                        .replyMarkup(menuBuilder.buildMainMenu())
                        .parseMode("Markdown")
                        .build();
            }

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("""
                          ✅ Ключи успешно сохранены и проверены!

                          Теперь вы можете начать торговать. 🚀
                          """)
                    .replyMarkup(menuBuilder.buildMainMenu())
                    .parseMode("Markdown")
                    .build();

        } catch (Exception e) {
            apiKeySetupService.completeSetup(chatId);
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("❌ Ошибка при проверке подключения:\n" + e.getMessage())
                    .replyMarkup(menuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }
    }
}
