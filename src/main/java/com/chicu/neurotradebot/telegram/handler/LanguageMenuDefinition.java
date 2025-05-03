package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.view.LanguageMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class LanguageMenuDefinition implements MenuDefinition {

    private final LanguageMenuBuilder builder;

    @Override
    public Set<String> keys() {
        return Set.of("language_menu", "select_language");
    }

    @Override
    public String title() {
        return "Выбор языка:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        Long currentChatId = BotContext.getChatId();
        return builder.buildLanguageMenu();
    }
}
