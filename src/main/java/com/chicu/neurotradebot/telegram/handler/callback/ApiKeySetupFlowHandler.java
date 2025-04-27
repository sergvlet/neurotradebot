package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.exchange.core.ExchangeClient;
import com.chicu.neurotradebot.exchange.core.ExchangeRegistry;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import com.chicu.neurotradebot.user.entity.ExchangeCredential;
import com.chicu.neurotradebot.user.entity.ExchangeSettings;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.enums.ApiKeySetupStage;
import com.chicu.neurotradebot.user.repository.ExchangeCredentialRepository;
import com.chicu.neurotradebot.user.repository.ExchangeSettingsRepository;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.user.service.ApiKeySetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeySetupFlowHandler {

    private final ApiKeySetupService apiKeySetupService;
    private final ExchangeCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final ExchangeRegistry exchangeRegistry;
    private final StartMenuBuilder menuBuilder;
    private final ExchangeSettingsRepository settingsRepository;

    public BotApiMethod<?> handle(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        String text = message.getText();

        ApiKeySetupStage stage = apiKeySetupService.getUserStage(chatId);

        if (stage == ApiKeySetupStage.ENTER_API_KEY) {
            apiKeySetupService.saveTempApiKey(chatId, text);
            apiKeySetupService.setUserStage(chatId, ApiKeySetupStage.ENTER_SECRET_KEY);

            log.info("[{}] API KEY сохранён, ожидаем SECRET KEY", chatId);

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("""
                          🔒 Отлично!

                          Теперь отправьте ваш *SECRET KEY* (секретный ключ) одним сообщением:
                          """)
                    .parseMode("Markdown")
                    .build();
        } else if (stage == ApiKeySetupStage.ENTER_SECRET_KEY) {
            String apiKey = apiKeySetupService.getTempApiKey(chatId);
            String selectedExchange = apiKeySetupService.getSelectedExchange(chatId);

            if (apiKey == null || selectedExchange == null) {
                log.warn("[{}] Ошибка данных при сохранении ключей", chatId);
                return SendMessage.builder()
                        .chatId(String.valueOf(chatId))
                        .text("⚠️ Ошибка данных. Попробуйте начать настройку заново.")
                        .parseMode("Markdown")
                        .build();
            }

            User user = userRepository.findById(chatId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден."));

            ExchangeCredential credential = credentialRepository.findByUserIdAndExchange(user.getId(), selectedExchange)
                    .orElseGet(() -> {
                        ExchangeCredential newCredential = new ExchangeCredential();
                        newCredential.setUser(user);
                        newCredential.setExchange(selectedExchange);
                        newCredential.setCreatedAt(LocalDateTime.now());
                        return newCredential;
                    });

            boolean useTestnet = settingsRepository.findByUserIdAndExchange(chatId, selectedExchange)
                    .map(ExchangeSettings::getUseTestnet)
                    .orElse(true);

            if (useTestnet) {
                credential.setTestApiKey(apiKey);
                credential.setTestSecretKey(text);
                log.info("[{}] Сохраняем ключи в TESTNET", chatId);
            } else {
                credential.setRealApiKey(apiKey);
                credential.setRealSecretKey(text);
                log.info("[{}] Сохраняем ключи в REAL", chatId);
            }

            credentialRepository.save(credential);

            try {
                ExchangeClient client = exchangeRegistry.getClient(selectedExchange);
                boolean isConnected = client.testConnection(chatId);

                apiKeySetupService.completeSetup(chatId);

                if (!isConnected) {
                    log.warn("[{}] Подключение не удалось", chatId);
                    return EditMessageText.builder()
                            .chatId(String.valueOf(chatId))
                            .messageId(messageId)
                            .text("""
                                  ❌ Подключение к бирже не удалось.

                                  Проверьте правильность введённых ключей.
                                  """)
                            .parseMode("Markdown")
                            .replyMarkup(menuBuilder.buildMainMenu())
                            .build();
                }

                log.info("[{}] Ключи успешно проверены и сохранены", chatId);
                return EditMessageText.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(messageId)
                        .text("""
                              ✅ Ключи успешно сохранены и проверены!

                              Теперь вы можете начать торговать. 🚀
                              """)
                        .parseMode("Markdown")
                        .replyMarkup(menuBuilder.buildMainMenu())
                        .build();
            } catch (Exception e) {
                apiKeySetupService.completeSetup(chatId);
                log.error("[{}] Ошибка проверки подключения: {}", chatId, e.getMessage(), e);
                return EditMessageText.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(messageId)
                        .text("❌ Ошибка проверки подключения: " + e.getMessage())
                        .parseMode("Markdown")
                        .replyMarkup(menuBuilder.buildMainMenu())
                        .build();
            }
        } else {
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("⚠️ Неверная стадия настройки. Попробуйте заново.")
                    .parseMode("Markdown")
                    .build();
        }
    }
}
