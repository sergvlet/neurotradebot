package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class TradingMenuCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;

    @Override
    public BotCallback callback() {
        return BotCallback.TRADING_MENU;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // Отображаем меню выбора режима торговли
        String text = "🔄 Выберите режим торговли:";
        var keyboard = keyboardService.getTradingMenu(chatId);  // Это меню для выбора AI или Manual режима
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
