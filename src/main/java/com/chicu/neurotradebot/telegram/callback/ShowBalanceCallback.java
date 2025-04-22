package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.binance.BinanceAccountService;
import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class ShowBalanceCallback implements CallbackProcessor {

    private final BinanceAccountService binanceAccountService;
    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    @Override
    public BotCallback callback() {
        return BotCallback.SHOW_BALANCE;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        String text = binanceAccountService.getFormattedBalance(chatId);
        var keyboard = keyboardService.getSettingsMenu(chatId);

        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
