package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.view.SubscriptionMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SubscriptionMenuDefinition implements MenuDefinition {

    private final SubscriptionMenuBuilder builder;

    @Override
    public Set<String> keys() {
        return Set.of("subscribe_menu", "start_subscriptions");
    }

    @Override
    public String title() {
        return "Управление подписками:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        Long currentChatId = BotContext.getChatId();
        return builder.buildSubscriptionMenu();
    }
}
