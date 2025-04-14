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
public class StrategyToggleCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.TOGGLE_STRATEGY;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // Пример callbackData: "toggle_strategy:SMA"
        String[] parts = callbackData.split(":");
        if (parts.length != 2) return;

        String strategyKey = parts[1];
        AvailableStrategy strategy;
        try {
            strategy = AvailableStrategy.valueOf(strategyKey);
        } catch (IllegalArgumentException e) {
            return;
        }

        // Переключаем стратегию
        userSettingsService.toggleStrategy(chatId, strategy);

        // Получаем обновлённые стратегии
        Set<AvailableStrategy> selected = userSettingsService.getSelectedStrategies(chatId);
        String text = "🧠 Выберите стратегии для торговли:";
        var keyboard = keyboardService.getStrategySelectionMenu(selected);

        // Обновляем сообщение
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
