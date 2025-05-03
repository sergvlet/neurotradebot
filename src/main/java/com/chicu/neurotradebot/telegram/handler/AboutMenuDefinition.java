// src/main/java/com/chicu/neurotradebot/telegram/handler/AboutMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.view.AboutBotMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AboutMenuDefinition implements MenuDefinition {

    private final AboutBotMenuBuilder builder;

    @Override
    public Set<String> keys() {
        return Set.of("about_bot");
    }

    @Override
    public String title() {
        return "О боте:";
    }

    /**
     * @param chatId текущий chatId (не используется здесь)
     */
    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return builder.buildAboutMenu();
    }
}
