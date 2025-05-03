// src/main/java/com/chicu/neurotradebot/telegram/handler/NetworkSettingsMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.view.NetworkSettingsMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NetworkSettingsMenuDefinition implements MenuDefinition {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final NetworkSettingsMenuBuilder builder;

    @Override
    public Set<String> keys() {
        return Set.of("settings", "back_to_settings", "select_ai_mode", "select_manual_mode");
    }

    @Override
    public String title() {
        return "Настройки сети:";
    }

    /**
     * @param chatId текущий chatId
     */
    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // берем пользователя и его настройки
        var user     = userService.getOrCreate(chatId);
        var settings = settingsService.getOrCreate(user);
        // передаем текущий testMode, чтобы билдера отметила нужную кнопку
        return builder.buildNetworkSettingsMenu(settings.isTestMode());
    }
}
