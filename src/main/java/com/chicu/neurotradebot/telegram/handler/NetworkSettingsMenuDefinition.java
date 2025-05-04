package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.entity.User;
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
        return Set.of(
                "settings",
                "back_to_settings",
                "select_ai_mode",        // ← теперь они вызывают это меню
                "select_manual_mode"
        );
    }


    @Override
    public String title() {
        return "Настройки сети:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        Long currentChatId = BotContext.getChatId();
        var user = userService.getOrCreate(currentChatId);
        var settings = settingsService.getOrCreate(user);
        return builder.buildNetworkSettingsMenu(settings.isTestMode(), settings.getExchange());
    }


}
