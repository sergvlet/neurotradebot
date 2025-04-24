package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class StrategySelectionCallback implements CallbackProcessor {

    private final UserSettingsService userSettingsService;
    private final MessageUtils messageUtils;

    @Override
    public BotCallback callback() {
        return BotCallback.TOGGLE_STRATEGY;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // Получаем имя стратегии из callbackData
        String[] parts = callbackData.split(":");
        String strategyName = parts[1]; // Имя стратегии, например "Adx"

        // Получаем текущие настройки пользователя
        var settings = userSettingsService.getOrCreate(chatId);

        // Логика для добавления или удаления стратегии из выбранных
        if (settings.getStrategies().contains(strategyName)) {
            // Если стратегия уже выбрана, удаляем её
            settings.getStrategies().remove(strategyName);
        } else {
            // Если стратегия не выбрана, добавляем её
            settings.getStrategies().add(AvailableStrategy.valueOf(strategyName));
        }

        // Обновляем настройки пользователя
        userSettingsService.save(settings);

        // Отправляем обновленное сообщение
        String selectedStrategies = settings.getStrategies().toString();
        String text = "Вы выбрали следующие стратегии: " + selectedStrategies;

        messageUtils.editMessage(chatId, messageId, text, null, sender);
    }
}
