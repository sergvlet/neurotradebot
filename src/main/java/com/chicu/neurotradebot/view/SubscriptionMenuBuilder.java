package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.List;

@Component
public class SubscriptionMenuBuilder {
    public InlineKeyboardMarkup buildSubscriptionMenu() {
        InlineKeyboardButton add = InlineKeyboardButton.builder()
                .text("➕ Добавить подписку")
                .callbackData("subscribe_add")
                .build();
        InlineKeyboardButton remove = InlineKeyboardButton.builder()
                .text("➖ Удалить подписку")
                .callbackData("subscribe_remove")
                .build();
        InlineKeyboardButton list = InlineKeyboardButton.builder()
                .text("📜 Мои подписки")
                .callbackData("subscribe_list")
                .build();
        InlineKeyboardButton back = InlineKeyboardButton.builder()
                .text("🔙 Назад")
                .callbackData("back_to_main")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                    List.of(add),
                    List.of(remove),
                    List.of(list),
                    List.of(back)
                ))
                .build();
    }
}
