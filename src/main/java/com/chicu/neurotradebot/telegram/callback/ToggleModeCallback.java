package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class ToggleModeCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.TOGGLE_MODE;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // Извлекаем режим из callbackData, например "toggle_mode:REAL"
        if (callbackData == null || !callbackData.contains(":")) return;

        String modeName = callbackData.split(":")[1];
        TradeMode selectedMode = TradeMode.valueOf(modeName);

        // Сохраняем выбранный режим
        userSettingsService.setTradeMode(chatId, selectedMode);

        // Отображаем обновлённое меню
        InlineKeyboardMarkup keyboard = keyboardService.getModeSelectionMenu(chatId, selectedMode);
        String text = "🧪 Выберите режим торговли:\n\n" + selectedMode.getTitle();
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
