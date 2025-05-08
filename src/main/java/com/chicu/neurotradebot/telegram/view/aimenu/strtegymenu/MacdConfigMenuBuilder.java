// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/MacdConfigMenuBuilder.java
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

import java.util.List;

@Component
@RequiredArgsConstructor
public class MacdConfigMenuBuilder {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;

    // стандартные параметры MACD: fast=12, slow=26, signal=9
    private static final RsiMacdConfig DEFAULT = RsiMacdConfig.builder()
        .macdFast(12)
        .macdSlow(26)
        .macdSignal(9)
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
            ? "*Дефолтные* настройки MACD\n"
            : "*Пользовательские* настройки MACD\n");
        text.append("• fast: ").append(c.getMacdFast()).append("\n");
        text.append("• slow: ").append(c.getMacdSlow()).append("\n");
        text.append("• signal: ").append(c.getMacdSignal()).append("\n\n");

        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder().text("– fast").callbackData("macd:decFast").build(),
                    InlineKeyboardButton.builder().text("fast").callbackData("macd:menu").build(),
                    InlineKeyboardButton.builder().text("+ fast").callbackData("macd:incFast").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("– slow").callbackData("macd:decSlow").build(),
                    InlineKeyboardButton.builder().text("slow").callbackData("macd:menu").build(),
                    InlineKeyboardButton.builder().text("+ slow").callbackData("macd:incSlow").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("– signal").callbackData("macd:decSignal").build(),
                    InlineKeyboardButton.builder().text("signal").callbackData("macd:menu").build(),
                    InlineKeyboardButton.builder().text("+ signal").callbackData("macd:incSignal").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("🔄 сбросить дефолт").callbackData("macd:reset").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("◀️ Назад").callbackData("strategies:menu").build()
                )
            ))
            .build();

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
