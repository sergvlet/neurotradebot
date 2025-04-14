package com.chicu.neurotradebot.telegram.util;

import com.chicu.neurotradebot.telegram.callback.BotCallback;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Service
public class KeyboardService {

    public InlineKeyboardMarkup getMainMenu(Long chatId) {
        return buildKeyboard(List.of(
                List.of(createButton("🚀 Начать", BotCallback.START_TRADE.name())),
                List.of(createButton("⚙️ Настройки", BotCallback.SETTINGS.name())),
                List.of(createButton("💳 Подписка", BotCallback.SUBSCRIBE.name())),
                List.of(createButton("🤖 О боте", BotCallback.BOT_INFO.name()))
        ));
    }

    public InlineKeyboardMarkup buildKeyboard(List<List<InlineKeyboardButton>> buttons) {
        return InlineKeyboardMarkup.builder().keyboard(buttons).build();
    }

    public InlineKeyboardButton createButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
