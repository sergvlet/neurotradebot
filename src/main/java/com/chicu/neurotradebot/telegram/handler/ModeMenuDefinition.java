package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.view.ModeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class ModeMenuDefinition {

    private final ModeMenuBuilder builder;
    private final UserService userService;
    private final AiTradeSettingsService settingsService;

    /**
     * Возвращает меню выбора режима (реал/тест) на основе текущих настроек пользователя.
     */
    public InlineKeyboardMarkup createMenuFromCurrentSettings() {
        Long chatId = BotContext.getChatId();
        User user = userService.getOrCreate(chatId);
        boolean testMode = settingsService.getOrCreate(user).isTestMode();
        return builder.buildModeMenu(testMode);
    }

    /**
     * Возвращает меню выбора режима по заданному признаку testMode.
     */
    public InlineKeyboardMarkup createMenu(boolean testMode) {
        return builder.buildModeMenu(testMode);
    }
}
