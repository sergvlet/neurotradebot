// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/MacdConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.MacdConfig;
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

    // Дефолтные параметры MACD: fast=12, slow=26, signal=9
    private static final MacdConfig DEFAULT = MacdConfig.builder()
        .fast(12)
        .slow(26)
        .signal(9)
        .build();

    public MacdConfig getDefaultConfig() {
        return DEFAULT;
    }

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        MacdConfig cfg = settings.getMacdConfig();
        if (cfg == null) {
            // Если ещё нет конфигурации — создаём с дефолтами
            cfg = MacdConfig.builder()
                .fast(DEFAULT.getFast())
                .slow(DEFAULT.getSlow())
                .signal(DEFAULT.getSignal())
                .build();
            cfg.setSettings(settings);
            settings.setMacdConfig(cfg);
            settingsService.save(settings);
        }

        boolean isDefault = cfg.getFast() == DEFAULT.getFast()
                         && cfg.getSlow() == DEFAULT.getSlow()
                         && cfg.getSignal() == DEFAULT.getSignal();

        StringBuilder text = new StringBuilder();
        text.append(isDefault
            ? "*Дефолтные* настройки MACD\n"
            : "*Пользовательские* настройки MACD\n");
        text.append("• fast: ").append(cfg.getFast()).append("\n");
        text.append("• slow: ").append(cfg.getSlow()).append("\n");
        text.append("• signal: ").append(cfg.getSignal()).append("\n\n");

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
