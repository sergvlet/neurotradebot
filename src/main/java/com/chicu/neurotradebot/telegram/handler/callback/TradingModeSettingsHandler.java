package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TradingModeSettingsHandler {

    private final StartMenuBuilder startMenuBuilder;

    public EditMessageText handle(long chatId, Integer messageId) {

        InlineKeyboardButton manualButton = InlineKeyboardButton.builder()
                .text("🔹 Ручная торговля")
                .callbackData("MANUAL_TRADING")
                .build();

        InlineKeyboardButton aiButton = InlineKeyboardButton.builder()
                .text("🔹 AI торговля")
                .callbackData("AI_TRADING")
                .build();

        InlineKeyboardMarkup settingsMenu = InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(manualButton),
                        List.of(aiButton)
                ))
                .build();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("⚙️ Выберите режим торговли:")
                .replyMarkup(settingsMenu)
                .build();
    }
}
