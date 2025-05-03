// src/main/java/com/chicu/neurotradebot/telegram/handler/StartMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.view.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StartMenuDefinition implements MenuDefinition {

    private final StartMenuBuilder builder;

    @Override
    public Set<String> keys() {
        return Set.of("/start", "back_to_main");
    }

    @Override
    public String title() {
        return "Главное меню:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // chatId тут не нужен, но обязателен по сигнатуре
        return builder.buildMainMenu();
    }
}
