package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class TimeframeSetCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.SET_TIMEFRAME;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        String tf = callbackData.split(":")[1];
        userSettingsService.setTimeframe(chatId, tf);

        String text = "✅ Таймфрейм установлен: " + tf;
        var keyboard = keyboardService.getSettingsMenu(chatId);
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
