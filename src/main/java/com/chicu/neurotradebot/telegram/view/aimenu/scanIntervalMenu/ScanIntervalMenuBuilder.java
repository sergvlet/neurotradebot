// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/ScanIntervalMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.scanIntervalMenu;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

@Component
public class ScanIntervalMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_scan_interval");
    }

    @Override
    public String title() {
        return "⏱ Выберите интервал сканирования:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder()
                        .text("1 мин")
                        .callbackData("scan_1m")
                        .build(),
                    InlineKeyboardButton.builder()
                        .text("5 мин")
                        .callbackData("scan_5m")
                        .build()
                ),
                List.of(
                    InlineKeyboardButton.builder()
                        .text("15 мин")
                        .callbackData("scan_15m")
                        .build(),
                    InlineKeyboardButton.builder()
                        .text("1 час")
                        .callbackData("scan_1h")
                        .build()
                ),
                List.of(
                    InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("ai_control")
                        .build()
                )
            ))
            .build();
    }
    
}
