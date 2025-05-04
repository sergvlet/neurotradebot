package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.view.ManualTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ManualTradeMenuDefinition implements MenuDefinition {

    private final ManualTradeMenuBuilder builder;

    @Override
    public Set<String> keys() {
        return Set.of("manual_trade_menu");
    }

    @Override
    public String title() {
        return "Ручная торговля:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return builder.build(chatId);
    }
}
