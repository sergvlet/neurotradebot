package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class ManualMenuCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    @Override
    public BotCallback callback() {
        return BotCallback.MANUAL_MENU;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        messageUtils.editMessage(
                chatId,
                messageId,
                "📉 <b>Ручная торговля</b>\n\nВыберите действие:",
                keyboardService.getManualTradingMenu(chatId),
                sender
        );
    }
}
