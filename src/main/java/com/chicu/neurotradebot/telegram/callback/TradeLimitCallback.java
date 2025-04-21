package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class TradeLimitCallback implements CallbackProcessor {

    private final UserSettingsService userSettingsService;
    private final MessageUtils messageUtils;

    @Override
    public BotCallback callback() {
        return BotCallback.TRADE_LIMIT;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        userSettingsService.setWaitingForInput(chatId, "TRADE_LIMIT");
        messageUtils.editMessage(chatId, messageId,
                "💵 Введите лимит сделки в USDT (например: 25 или 100):", null, sender);
    }
}
