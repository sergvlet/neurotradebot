package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class ManualTradeSettingsCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;

    @Override
    public BotCallback callback() {
        return BotCallback.MANUAL_TRADE_SETTINGS;
    }

    @Override
    public void process(Long chatId, Integer messageId, String data, AbsSender sender) {
        messageUtils.editMessage(chatId, messageId,
                "⚙️ <b>Настройки ручной торговли</b>",
                keyboardService.getManualTradeSettingsMenu(chatId),
                sender);
    }
}
