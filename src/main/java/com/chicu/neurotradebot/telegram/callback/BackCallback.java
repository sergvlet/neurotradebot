package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class BackCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    @Override
    public BotCallback callback() {
        return BotCallback.BACK;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // Для кнопки "Назад" callbackData не используется, просто возвращаем главное меню
        messageUtils.editMessage(
                chatId,
                messageId,
                "🏠 Главное меню:\nВыберите действие ниже:",
                keyboardService.getMainMenu(chatId),
                sender
        );
    }
}
