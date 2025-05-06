// src/main/java/com/chicu/neurotradebot/view/NetworkSettingsViewBuilder.java
package com.chicu.neurotradebot.telegram.view.networksettingsmenu;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
public class NetworkSettingsViewBuilder {
    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final ApiCredentialsService credentialsService;

    public NetworkSettingsViewBuilder(UserService userService,
                                      AiTradeSettingsService settingsService,
                                      ApiCredentialsService credentialsService) {
        this.userService = userService;
        this.settingsService = settingsService;
        this.credentialsService = credentialsService;
    }

    public String title() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        var settings = settingsService.getOrCreate(user);

        String exch = settings.getExchange() != null ? settings.getExchange() : "не выбрана";
        String mode = settings.isTestMode() ? "TESTNET" : "REAL";

        // Смотрим, есть ли ключи именно для этих настроек
        List<ApiCredentials> creds = credentialsService.listCredentials(
                user, exch, settings.isTestMode()
        );

        String label = creds.stream()
                .filter(ApiCredentials::isActive)
                .map(ApiCredentials::getLabel)
                .findFirst()
                .orElse("–");

        String status;
        if (creds.isEmpty()) {
            status = "⚠️ Нет сохранённых ключей";
        } else {
            try {
                boolean ok = credentialsService.testConnection(user, exch, settings.isTestMode());
                status = ok ? "✅ Подключение успешно" : "❌ Ошибка подключения";
            } catch (Exception ex) {
                // Выводим текст ошибки, не падаем
                status = "❌ " + ex.getMessage();
            }
        }

        return "⚙️ Настройки сети:\n"
                + "Биржа: " + exch + "\n"
                + "Режим: "  + mode + "\n"
                + "Ключ: "  + label + "\n"
                + status;
    }

    /**
     * @param fromAi true — пришли из AI-меню; false — из Manual-меню
     */
    public InlineKeyboardMarkup markup(Long chatId, boolean fromAi) {
        var settings = settingsService.getOrCreate(userService.getOrCreate(chatId));

        InlineKeyboardButton toggle = InlineKeyboardButton.builder()
                .text(settings.isTestMode() ? "🔵 Тестнет (✓)" : "🟢 Реал (✓)")
                .callbackData("toggle_mode")
                .build();

        InlineKeyboardButton exchange = InlineKeyboardButton.builder()
                .text("🌐 Выбрать биржу")
                .callbackData("select_exchange")
                .build();

        InlineKeyboardButton apiSetup = InlineKeyboardButton.builder()
                .text("🔑 Настроить API-ключи")
                .callbackData("api_setup_start")
                .build();

        String applyData = fromAi
                ? "apply_network_settings_ai"
                : "apply_network_settings_manual";

        InlineKeyboardButton apply = InlineKeyboardButton.builder()
                .text("✅ Оставить текущие настройки")
                .callbackData(applyData)
                .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("🔙 Отмена")
                .callbackData(fromAi ? "ai_control" : "manual_trade_menu")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(toggle),
                        List.of(exchange),
                        List.of(apiSetup),
                        List.of(apply),
                        List.of(back)
                ))
                .build();
    }
}
