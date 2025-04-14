package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StrategyMenuCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.STRATEGY_MENU;
    }

    @Override
    public void process(Long chatId, Integer messageId, AbsSender sender) {
        Set<AvailableStrategy> selected = userSettingsService.getSelectedStrategies(chatId);
        var keyboard = keyboardService.getStrategySelectionMenu(selected);
        String text = "🧠 Выберите стратегии для торговли:";
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
