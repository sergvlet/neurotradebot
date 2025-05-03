// src/main/java/com/chicu/neurotradebot/telegram/handler/AiTradeMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.view.AITradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AiTradeMenuDefinition implements MenuDefinition {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final AITradeMenuBuilder builder;

    @Override
    public Set<String> keys() {
        return Set.of("ai_trade_menu", "select_ai_mode");
    }

    @Override
    public String title() {
        return "AI Торговля:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // вместо BotContext берем chatId из аргумента
        User user = userService.getOrCreate(chatId);
        var settings = settingsService.getOrCreate(user);
        return builder.buildAiSettingsMenu(settings);
    }
}
