// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/BollingerConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.BollingerConfig;
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
public class BollingerConfigMenuBuilder {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;

    // дефолтные параметры Bollinger: period=20, stdDevMultiplier=2
    private static final BollingerConfig DEFAULT = BollingerConfig.builder()
        .period(20)
        .stdDevMultiplier(BigDecimal.valueOf(2))
        .build();

    public BollingerConfig getDefaultConfig() {
        return DEFAULT;
    }

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        BollingerConfig cfg = settings.getBollingerConfig();
        if (cfg == null) {
            BollingerConfig def = new BollingerConfig();
            def.setPeriod(DEFAULT.getPeriod());
            def.setStdDevMultiplier(DEFAULT.getStdDevMultiplier());
            def.setSettings(settings);
            settings.setBollingerConfig(def);
            settingsService.save(settings);
            cfg = def;
        }

        boolean isDefault =
            cfg.getPeriod() == DEFAULT.getPeriod() &&
            cfg.getStdDevMultiplier().compareTo(DEFAULT.getStdDevMultiplier()) == 0;

        StringBuilder text = new StringBuilder();
        text.append(isDefault
            ? "*Дефолтные* настройки Bollinger Bands\n"
            : "*Пользовательские* настройки Bollinger Bands\n");
        text.append("• period: ").append(cfg.getPeriod()).append("\n");
        text.append("• stdDevMultiplier: ").append(cfg.getStdDevMultiplier().toPlainString()).append("\n\n");

        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder().text("– period").callbackData("bollinger:decPeriod").build(),
                    InlineKeyboardButton.builder().text("period").callbackData("bollinger:menu").build(),
                    InlineKeyboardButton.builder().text("+ period").callbackData("bollinger:incPeriod").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("– multiplier").callbackData("bollinger:decMultiplier").build(),
                    InlineKeyboardButton.builder().text("multiplier").callbackData("bollinger:menu").build(),
                    InlineKeyboardButton.builder().text("+ multiplier").callbackData("bollinger:incMultiplier").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("🔄 сбросить дефол").callbackData("bollinger:reset").build()
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
