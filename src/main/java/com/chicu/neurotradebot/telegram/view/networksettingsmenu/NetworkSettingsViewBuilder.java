// src/main/java/com/chicu/neurotradebot/telegram/view/networksettingsmenu/NetworkSettingsViewBuilder.java
package com.chicu.neurotradebot.telegram.view.networksettingsmenu;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
     // <-- нужен этот импорт
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import com.chicu.neurotradebot.telegram.navigation.NavigationHistoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Set;

@Component
public class NetworkSettingsViewBuilder implements MenuDefinition {
    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final ApiCredentialsService credentialsService;
    private final NavigationHistoryService history;
    public NetworkSettingsViewBuilder(UserService userService,
                                      AiTradeSettingsService settingsService,
                                      ApiCredentialsService credentialsService,
                                      NavigationHistoryService history) {
        this.userService = userService;
        this.settingsService = settingsService;
        this.credentialsService = credentialsService;

        this.history = history;
    }

    @Override
    public Set<String> keys() {
        // уникальный ключ этого меню
        return Set.of("network_settings");
    }

    @Override
    public String title() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        var settings = settingsService.getOrCreate(user);

        String exch = settings.getExchange() != null ? settings.getExchange() : "не выбрана";
        String mode = settings.isTestMode() ? "TESTNET" : "REAL";

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
                status = "❌ " + ex.getMessage();
            }
        }

        return "⚙️ Настройки сети:\n"
                + "Биржа: " + exch + "\n"
                + "Режим: "  + mode + "\n"
                + "Ключ: "  + label + "\n"
                + status;
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // регистрируем вход в меню
        history.push(chatId, "network_settings");

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

        String applyData = "apply_network_settings_ai"; // или manual, если нужно

        InlineKeyboardButton apply = InlineKeyboardButton.builder()
                .text("✅ Оставить текущие настройки")
                .callbackData(applyData)
                .build();

        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("🔙 Отмена")
                .callbackData("start_menu")
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
