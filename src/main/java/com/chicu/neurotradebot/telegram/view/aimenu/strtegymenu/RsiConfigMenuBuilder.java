// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/RsiConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RsiConfigMenuBuilder {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;

    // дефолтный RsiMacdConfig
    private static final RsiMacdConfig DEFAULT = RsiMacdConfig.builder()
        .rsiPeriod(14)
        .rsiLower(BigDecimal.valueOf(30))
        .rsiUpper(BigDecimal.valueOf(70))
        .build();

    public RsiMacdConfig getDefaultConfig() {
        return DEFAULT;
    }

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        RsiMacdConfig c = cfg.getRsiMacdConfig();
        boolean isDefault = c.equals(DEFAULT);

        StringBuilder text = new StringBuilder();
        text.append(isDefault 
            ? "*Дефолтные* настройки RSI\n" 
            : "*Пользовательские* настройки RSI\n");
        text.append("• period: ").append(c.getRsiPeriod()).append("\n");
        text.append("• lower: ").append(c.getRsiLower()).append("\n");
        text.append("• upper: ").append(c.getRsiUpper()).append("\n\n");

        // Кнопки «+» и «–» для каждого параметра
        InlineKeyboardMarkup kb = new InlineKeyboardMarkup(List.of(
            List.of(
                InlineKeyboardButton.builder().text("– period").callbackData("rsi:decPeriod").build(),
                InlineKeyboardButton.builder().text("period").callbackData("rsi:menu").build(),
                InlineKeyboardButton.builder().text("+ period").callbackData("rsi:incPeriod").build()
            ),
            List.of(
                InlineKeyboardButton.builder().text("– lower").callbackData("rsi:decLower").build(),
                InlineKeyboardButton.builder().text("lower").callbackData("rsi:menu").build(),
                InlineKeyboardButton.builder().text("+ lower").callbackData("rsi:incLower").build()
            ),
            List.of(
                InlineKeyboardButton.builder().text("– upper").callbackData("rsi:decUpper").build(),
                InlineKeyboardButton.builder().text("upper").callbackData("rsi:menu").build(),
                InlineKeyboardButton.builder().text("+ upper").callbackData("rsi:incUpper").build()
            ),
            List.of(
                InlineKeyboardButton.builder().text("🔄 сбросить дефолт").callbackData("rsi:reset").build()
            ),
            List.of(
                InlineKeyboardButton.builder().text("◀️ Назад").callbackData("strategies:menu").build()
            )
        ));

        SendMessage msg = SendMessage.builder()
            .chatId(chatId.toString())
            .text(text.toString())
            .parseMode("Markdown")
            .replyMarkup(kb)
            .build();

        if (messageId == null) {
            sender.sendMessage(chatId, msg.getText(), kb);
        } else {
            sender.editMessage(chatId, messageId, msg.getText(), kb);
        }

    }

}
