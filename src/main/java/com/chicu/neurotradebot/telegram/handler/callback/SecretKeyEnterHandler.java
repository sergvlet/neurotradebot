package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.exchange.core.ExchangeClient;
import com.chicu.neurotradebot.exchange.core.ExchangeRegistry;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import com.chicu.neurotradebot.user.entity.ExchangeCredential;
import com.chicu.neurotradebot.user.enums.ApiKeySetupStage;
import com.chicu.neurotradebot.user.repository.ExchangeCredentialRepository;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.user.service.ApiKeySetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SecretKeyEnterHandler {

    private final ApiKeySetupService apiKeySetupService;
    private final ExchangeCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final ExchangeRegistry exchangeRegistry;
    private final StartMenuBuilder menuBuilder;

    public EditMessageText handle(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        Integer messageId = message.getMessageId();

        if (apiKeySetupService.getUserStage(chatId) != ApiKeySetupStage.ENTER_SECRET_KEY) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("⚠️ Ошибка стадии. Пожалуйста, начните настройку заново.")
                    .parseMode("Markdown")
                    .build();
        }

        String secretKey = message.getText();
        String apiKey = apiKeySetupService.getTempApiKey(chatId);
        String selectedExchange = apiKeySetupService.getSelectedExchange(chatId);

        if (apiKey == null || selectedExchange == null) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("⚠️ Ошибка данных. Попробуйте заново.")
                    .parseMode("Markdown")
                    .build();
        }

        // Найти или создать запись в БД
        ExchangeCredential credential = credentialRepository.findByUserIdAndExchange(chatId, selectedExchange)
                .orElseGet(() -> {
                    ExchangeCredential newCredential = new ExchangeCredential();
                    newCredential.setUser(userRepository.findById(chatId)
                            .orElseThrow(() -> new RuntimeException("Пользователь не найден")));
                    newCredential.setExchange(selectedExchange);
                    newCredential.setCreatedAt(LocalDateTime.now());
                    return newCredential;
                });

        // Сохраняем в зависимости от режима
        if (apiKeySetupService.isUseTestnet(chatId)) {
            credential.setTestApiKey(apiKey);
            credential.setTestSecretKey(secretKey);
        } else {
            credential.setRealApiKey(apiKey);
            credential.setRealSecretKey(secretKey);
        }

        credentialRepository.save(credential);
        apiKeySetupService.completeSetup(chatId);

        // Проверка подключения
        try {
            ExchangeClient client = exchangeRegistry.getClient(selectedExchange);
            boolean isConnected = client.testConnection(chatId);

            if (!isConnected) {
                return EditMessageText.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(messageId)
                        .text("""
                              ❌ Подключение к бирже не удалось.

                              Проверьте правильность введённых ключей.
                              """)
                        .replyMarkup(menuBuilder.buildMainMenu())
                        .parseMode("Markdown")
                        .build();
            }

            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                          ✅ Ключи успешно сохранены и проверены!

                          Теперь вы можете начать торговать. 🚀
                          """)
                    .replyMarkup(menuBuilder.buildMainMenu())
                    .parseMode("Markdown")
                    .build();

        } catch (Exception e) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("❌ Ошибка при проверке подключения:\n\n" + e.getMessage())
                    .replyMarkup(menuBuilder.buildSettingsMenu())
                    .parseMode("Markdown")
                    .build();
        }
    }
}
