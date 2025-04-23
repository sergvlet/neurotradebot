package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.telegram.util.NavigationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class SettingsCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;
    private final NavigationHistoryService historyService;

    @Override
    public BotCallback callback() {
        return BotCallback.SETTINGS;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // сохраняем в историю переход
        historyService.push(chatId, callback());

        String text = "⚙️ Настройки:\nВыберите параметр для изменения:";
        InlineKeyboardMarkup keyboard = keyboardService.getManualTradeSettingsMenu(chatId);

        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
