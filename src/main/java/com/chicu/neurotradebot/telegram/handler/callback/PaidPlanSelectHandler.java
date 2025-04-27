package com.chicu.neurotradebot.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaidPlanSelectHandler {

    public EditMessageText handle(long chatId, Integer messageId) {
        InlineKeyboardButton oneMonth = InlineKeyboardButton.builder()
                .text("📅 1 месяц — 20 USDT")
                .callbackData("PLAN_1M")
                .build();

        InlineKeyboardButton threeMonths = InlineKeyboardButton.builder()
                .text("📅 3 месяца — 55 USDT")
                .callbackData("PLAN_3M")
                .build();

        InlineKeyboardButton sixMonths = InlineKeyboardButton.builder()
                .text("📅 6 месяцев — 100 USDT")
                .callbackData("PLAN_6M")
                .build();

        InlineKeyboardButton twelveMonths = InlineKeyboardButton.builder()
                .text("📅 12 месяцев — 180 USDT")
                .callbackData("PLAN_12M")
                .build();

        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("MAIN_MENU") // ✅ новая правильная цель - вернуться в главное меню
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(oneMonth),
                        List.of(threeMonths),
                        List.of(sixMonths),
                        List.of(twelveMonths),
                        List.of(backButton)
                ))
                .build();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                      📋 Выберите срок подписки:
                      """)
                .replyMarkup(markup)
                .build();
    }
}
