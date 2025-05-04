// src/main/java/com/chicu/neurotradebot/telegram/handler/ApiSetupMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.view.NetworkSettingsMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
    private final NetworkSettingsMenuBuilder menuBuilder;

    @Override
    public Set<String> keys() {
        return Set.of("api_setup_start");
    }

    @Override
    public String title() {
        return "Введите API ключ:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return null; // клавиатура не нужна — ожидается текст
    }

    @Override
    public void handleText(Update update) throws Exception {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        BotContext.setChatId(chatId);
        try {
            User user = userService.getOrCreate(chatId);
            var settings = settingsService.getOrCreate(user);

            String exchange = settings.getExchange();
            boolean testMode = settings.isTestMode();
            String step = settings.getApiSetupStep();

            if (step == null || step.equals("NONE")) {
                credentialsService.saveApiKey(user, exchange, testMode, text);
                settings.setApiSetupStep("WAIT_SECRET");
                settingsService.save(settings);

                sender.execute(SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Теперь введите API Secret:")
                        .build());

                log.info("✅ API Key принят для {} (testMode={})", exchange, testMode);

            } else if (step.equals("WAIT_SECRET")) {
                credentialsService.saveApiSecret(user, exchange, testMode, text);
                settings.setApiSetupStep("NONE");
                settingsService.save(settings);

                sender.execute(SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("✅ Ключи сохранены. Возвращаемся в настройки сети...")
                        .build());

                sender.execute(SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Настройки сети:")
                        .replyMarkup(menuBuilder.buildNetworkSettingsMenu(
                                settings.isTestMode(), settings.getExchange()))
                        .build());

                log.info("✅ API Secret сохранён и возвращено меню настроек для chatId={}", chatId);
            }

        } finally {
            BotContext.clear();
        }
    }
}
