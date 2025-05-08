// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/EmaConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.EmaCrossoverConfig;
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
public class EmaConfigMenuBuilder {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;

    // дефолтные параметры EMA Crossover: short=9, long=21
    private static final EmaCrossoverConfig DEFAULT = EmaCrossoverConfig.builder()
        .shortPeriod(9)
        .longPeriod(21)
        .build();

    public EmaCrossoverConfig getDefaultConfig() {
        return DEFAULT;
    }

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        EmaCrossoverConfig cfg = settings.getEmaCrossoverConfig();
        if (cfg == null) {
            EmaCrossoverConfig def = new EmaCrossoverConfig();
            def.setShortPeriod(DEFAULT.getShortPeriod());
            def.setLongPeriod(DEFAULT.getLongPeriod());
            def.setSettings(settings);
            settings.setEmaCrossoverConfig(def);
            settingsService.save(settings);
            cfg = def;
        }

        boolean isDefault =
            cfg.getShortPeriod() == DEFAULT.getShortPeriod() &&
            cfg.getLongPeriod() == DEFAULT.getLongPeriod();

        StringBuilder text = new StringBuilder();
        text.append(isDefault
            ? "*Дефолтные* настройки EMA Crossover\n"
            : "*Пользовательские* настройки EMA Crossover\n");
        text.append("• short: ").append(cfg.getShortPeriod()).append("\n");
        text.append("• long: ").append(cfg.getLongPeriod()).append("\n\n");

        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder().text("– short").callbackData("ema:decShort").build(),
                    InlineKeyboardButton.builder().text("short").callbackData("ema:menu").build(),
                    InlineKeyboardButton.builder().text("+ short").callbackData("ema:incShort").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("– long").callbackData("ema:decLong").build(),
                    InlineKeyboardButton.builder().text("long").callbackData("ema:menu").build(),
                    InlineKeyboardButton.builder().text("+ long").callbackData("ema:incLong").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("🔄 сбросить дефол").callbackData("ema:reset").build()
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
