package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class SelectModeCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.SELECT_MODE;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        TradeMode selected = userSettingsService.getTradeMode(chatId);

        if (selected == null) {
            selected = TradeMode.DEMO; // режим по умолчанию
            userSettingsService.setTradeMode(chatId, selected);
        }

        InlineKeyboardMarkup keyboard = keyboardService.getModeSelectionMenu(chatId, selected);
        String text = "🧪 Выберите режим торговли:\n\n" + selected.getTitle();

        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
