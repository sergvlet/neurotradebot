// src/main/java/com/chicu/neurotradebot/view/AboutBotMenuBuilder.java
package com.chicu.neurotradebot.view;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * Строит меню «О боте».
 */
@Component
public class AboutBotMenuBuilder {

    public InlineKeyboardMarkup buildAboutMenu() {
        // Здесь можно добавить дополнительные кнопки, например ссылка на документацию
        InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("🔙 Назад")
            .callbackData("back_to_main")
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(back)
            ))
            .build();
    }
}
