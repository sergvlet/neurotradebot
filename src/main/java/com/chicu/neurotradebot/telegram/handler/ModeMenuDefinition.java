// src/main/java/com/chicu/neurotradebot/telegram/handler/ModeMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.view.ModeMenuBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class ModeMenuDefinition {

    private final ModeMenuBuilder builder;

    public ModeMenuDefinition(ModeMenuBuilder builder) {
        this.builder = builder;
    }

    /**
     * Возвращает меню выбора режима (реал/тест).
     * @param testMode true — тестовый, false — реальный
     */
    public InlineKeyboardMarkup createMenu(boolean testMode) {
        return builder.buildModeMenu(testMode);
    }
}
