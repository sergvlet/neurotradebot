// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/MlTpSlConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.MlStrategyConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MlTpSlConfigMenuBuilder {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;

    private static final MlStrategyConfig DEFAULT = MlStrategyConfig.builder()
        .totalCapitalUsd(BigDecimal.valueOf(100))
        .entryRsiThreshold(32.0)
        .lookbackPeriod(Duration.ofHours(1))
        .predictUrl("http://localhost:5000/predict")
        .build();

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings s = settingsService.getByChatId(chatId);
        MlStrategyConfig cfg = s.getMlStrategyConfig();

        boolean isDefault =
            cfg.getTotalCapitalUsd().equals(DEFAULT.getTotalCapitalUsd()) &&
            cfg.getEntryRsiThreshold() == DEFAULT.getEntryRsiThreshold() &&
            cfg.getLookbackPeriod().equals(DEFAULT.getLookbackPeriod()) &&
            cfg.getPredictUrl().equals(DEFAULT.getPredictUrl());

        // Текст меню
        StringBuilder text = new StringBuilder();
        text.append(isDefault
            ? "*Дефолтные* настройки ML TP/SL\n"
            : "*Ваши* настройки ML TP/SL\n");
        text.append("• Капитал (USD): ").append(cfg.getTotalCapitalUsd()).append("\n");
        text.append("• Порог RSI: ").append(cfg.getEntryRsiThreshold()).append("\n");
        text.append("• Обратный отсчёт (баров): ").append(cfg.getLookbackPeriod().toHours()).append(" ч\n");
        text.append("• URL предикта: ").append(cfg.getPredictUrl()).append("\n\n");

        // Кнопки
        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                // капитал
                List.of(
                  InlineKeyboardButton.builder().text("– Капитал").callbackData("ml:decCapital").build(),
                  InlineKeyboardButton.builder().text("Капитал").callbackData("ml:menu").build(),
                  InlineKeyboardButton.builder().text("+ Капитал").callbackData("ml:incCapital").build()
                ),
                // RSI
                List.of(
                  InlineKeyboardButton.builder().text("– RSI").callbackData("ml:decRsi").build(),
                  InlineKeyboardButton.builder().text("RSI").callbackData("ml:menu").build(),
                  InlineKeyboardButton.builder().text("+ RSI").callbackData("ml:incRsi").build()
                ),
                // lookback
                List.of(
                  InlineKeyboardButton.builder().text("– Баров").callbackData("ml:decLookback").build(),
                  InlineKeyboardButton.builder().text("Бары").callbackData("ml:menu").build(),
                  InlineKeyboardButton.builder().text("+ Баров").callbackData("ml:incLookback").build()
                ),
                // URL
                List.of(
                  InlineKeyboardButton.builder().text("✏️ Изменить URL").callbackData("ml:setUrl").build()
                ),
                // сброс и назад
                List.of(
                  InlineKeyboardButton.builder().text("🔄 Сбросить все").callbackData("ml:reset").build(),
                  InlineKeyboardButton.builder().text("◀️ Назад").callbackData("ai_strategies").build()
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
