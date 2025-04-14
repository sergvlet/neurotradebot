package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class MainMenuCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;


    @Override
    public BotCallback callback() {
        return BotCallback.MAIN_MENU;
    }

    @Override
    public void process(Long chatId, Integer messageId, AbsSender sender) {
        String text = "🏠 Главное меню:\nВыберите действие ниже:";
        var keyboard = keyboardService.getMainMenu(chatId);

        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
