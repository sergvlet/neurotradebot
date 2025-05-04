// src/main/java/com/chicu/neurotradebot/telegram/handler/ApiSetupMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.entity.UserInputState;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.view.AiTradeMenuBuilder;
import com.chicu.neurotradebot.view.ManualTradeMenuBuilder;
import com.chicu.neurotradebot.view.NetworkSettingsMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiSetupMenuDefinition implements MenuDefinition, TextInputAwareMenu {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final ApiCredentialsService credentialsService;
    private final TelegramSender sender;
    private final ManualTradeMenuBuilder manualBuilder;
    private final AiTradeMenuBuilder aiBuilder;

    @Override
    public Set<String> keys() {
        return Set.of("api_setup_start");
    }

    @Override
    public String title() {
        return "🔐 Настройка API ключей";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return null;
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        BotContext.setChatId(chatId);
        try {
            User user = userService.getOrCreate(chatId);
            var settings = settingsService.getOrCreate(user);

            if (settings.getExchange() == null) {
                sender.sendMessage(chatId, "❗ Сначала выберите биржу.");
                return;
            }

            settings.setInputState(UserInputState.WAIT_API_KEY);
            settingsService.save(settings);

            log.info("📥 Установлено состояние WAIT_API_KEY для chatId={}", chatId);
            sender.sendMessage(chatId, "Введите API Key:");
        } catch (Exception e) {
            log.error("❌ Ошибка в ApiSetupMenuDefinition.handle(): {}", e.getMessage(), e);
            sender.sendMessage(chatId, "Ошибка при запуске настройки ключей.");
        } finally {
            BotContext.clear();
        }
    }

    @Override
    public boolean supports(UserInputState state) {
        return state == UserInputState.WAIT_API_KEY || state == UserInputState.WAIT_API_SECRET;
    }

    @Override
    public void handleText(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        BotContext.setChatId(chatId);
        try {
            User user = userService.getOrCreate(chatId);
            var settings = settingsService.getOrCreate(user);

            String exchange = settings.getExchange();
            boolean testMode = settings.isTestMode();
            UserInputState state = settings.getInputState();

            if (exchange == null) {
                sender.sendMessage(chatId, "❗ Сначала выберите биржу.");
                return;
            }

            if (state == UserInputState.WAIT_API_KEY) {
                credentialsService.saveApiKey(user, exchange, testMode, text);
                settings.setInputState(UserInputState.WAIT_API_SECRET);
                settingsService.save(settings);
                sender.sendMessage(chatId, "Теперь введите API Secret:");
                return;
            }

            if (state == UserInputState.WAIT_API_SECRET) {
                credentialsService.saveApiSecret(user, exchange, testMode, text);

                if (!credentialsService.testConnection(user, exchange, testMode)) {
                    sender.sendMessage(chatId, "❌ Ошибка подключения. Введите API Key заново:");
                    settings.setInputState(UserInputState.WAIT_API_KEY);
                    settingsService.save(settings);
                    return;
                }

                sender.sendMessage(chatId, "✅ Ключи сохранены и соединение успешно.");
                settings.setInputState(UserInputState.NONE);
                settingsService.save(settings);

                if (settings.isAiEnabled()) {
                    sender.sendMessage(chatId, aiBuilder.title(), aiBuilder.build(chatId));
                } else {
                    sender.sendMessage(chatId, manualBuilder.title(), manualBuilder.build(chatId));
                }
            }

        } catch (Exception e) {
            log.error("❌ Ошибка при вводе ключа: {}", e.getMessage(), e);
            sender.sendMessage(chatId, "Произошла ошибка. Попробуйте позже.");
        } finally {
            BotContext.clear();
        }
    }
}
