// src/main/java/com/chicu/neurotradebot/view/ApiSetupMenuBuilder.java
package com.chicu.neurotradebot.telegram.view;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class ApiSetupMenuBuilder {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final ApiCredentialsService credentialsService;

    public ApiSetupMenuBuilder(UserService userService,
                               AiTradeSettingsService settingsService,
                               ApiCredentialsService credentialsService) {
        this.userService = userService;
        this.settingsService = settingsService;
        this.credentialsService = credentialsService;
    }

    /**
     * Заголовок: если есть сохранённые ключи — спрашиваем «Заменить или оставить?»,
     * иначе — просим ввести новый KEY.
     */
    public String title() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        var cfg = settingsService.getOrCreate(user);
        boolean hasKey = !credentialsService
            .listCredentials(user, cfg.getExchange(), cfg.isTestMode())
            .isEmpty();

        return hasKey
            ? "🔑 API-ключи найдены. Заменить или оставить?"
            : "🔑 Введите новый API-Key:";
    }

    /**
     * Клавиатура: если нет ключей — одна кнопка «Ввести Key»,
     * иначе — две кнопки «Заменить» и «Оставить».
     */
    public InlineKeyboardMarkup markup() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        var cfg = settingsService.getOrCreate(user);

        boolean hasKey = !credentialsService
            .listCredentials(user, cfg.getExchange(), cfg.isTestMode())
            .isEmpty();

        if (!hasKey) {
            // Если ключей нет — предлагаем ввести API Key
            return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                    List.of(
                        InlineKeyboardButton.builder()
                            .text("🖊 Ввести Key")
                            .callbackData("enter_api_key")
                            .build()
                    )
                ))
                .build();
        }

        // Если ключи есть — две кнопки
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder()
                        .text("♻️ Заменить")
                        .callbackData("replace_api_key")
                        .build()
                ),
                List.of(
                    InlineKeyboardButton.builder()
                        .text("✅ Оставить")
                        .callbackData("keep_api_key")
                        .build()
                )
            ))
            .build();
    }
}
