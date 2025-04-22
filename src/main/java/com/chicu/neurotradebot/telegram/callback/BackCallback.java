package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class BackCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.BACK;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        var settings = userSettingsService.getOrCreate(chatId);

        // логика возврата: если ранее был вызван Settings → назад в TradingMenu
        if (callbackData.equals(BotCallback.BACK.getValue())) {
            // если пользователь уже настроен — возвращаем в торговое меню
            if (settings.getExchange() != null && settings.getStrategies() != null && !settings.getStrategies().isEmpty()) {
                messageUtils.editMessage(chatId, messageId,
                        "📊 Главное торговое меню:",
                        keyboardService.getTradingMenu(chatId),
                        sender);
            } else {
                // иначе — в главное меню
                messageUtils.editMessage(chatId, messageId,
                        "🏠 Главное меню:",
                        keyboardService.getMainMenu(chatId),
                        sender);
            }
        }
    }
}
