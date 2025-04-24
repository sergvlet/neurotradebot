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

        // Логика возврата в меню, проверяя, что пользователь настроил
        if (callbackData.equals(BotCallback.BACK.getValue())) {
            // Возвращаем в торговое меню, если все настройки готовы
            if (settings.getExchange() != null
                    && settings.getStrategies() != null && !settings.getStrategies().isEmpty()
                    && settings.getTradeMode() != null) {
                // Все нужные настройки присутствуют — показываем меню для торговли
                messageUtils.editMessage(chatId, messageId,
                        "📊 Главное торговое меню:",
                        keyboardService.getTradingMenu(chatId),
                        sender);
            } else {
                // Если что-то не настроено — показываем главное меню
                messageUtils.editMessage(chatId, messageId,
                        "🏠 Главное меню:",
                        keyboardService.getMainMenu(chatId),
                        sender);
            }
        }
    }
}
