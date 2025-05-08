// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/RsiConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiConfig;
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

    // дефолтные параметры RSI
    private static final RsiConfig DEFAULT = RsiConfig.builder()
        .period(14)
        .lower(BigDecimal.valueOf(30))
        .upper(BigDecimal.valueOf(70))
        .build();

    public RsiConfig getDefaultConfig() {
        return DEFAULT;
    }

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        RsiConfig cfg = settings.getRsiConfig();
        if (cfg == null) {
            // инициализация дефолтной конфигурации
            RsiConfig def = DEFAULT;
            def.setSettings(settings);
            settings.setRsiConfig(def);
            settingsService.save(settings);
            cfg = def;
        }

        // сравниваем BigDecimal через compareTo, а не equals (мираются разницы в scale)
        boolean isDefault =
            cfg.getPeriod() == DEFAULT.getPeriod() &&
            cfg.getLower().compareTo(DEFAULT.getLower()) == 0 &&
            cfg.getUpper().compareTo(DEFAULT.getUpper()) == 0;

        StringBuilder text = new StringBuilder();
        text.append(isDefault
            ? "*Дефолтные* настройки RSI\n"
            : "*Пользовательские* настройки RSI\n");
        text.append("• period: ").append(cfg.getPeriod()).append("\n");
        text.append("• lower: ").append(cfg.getLower().toPlainString()).append("\n");
        text.append("• upper: ").append(cfg.getUpper().toPlainString()).append("\n\n");

        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
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
                    InlineKeyboardButton.builder()
                        .text("🔄 сбросить дефол")
                        .callbackData("rsi:reset")
                        .build()
                ),
                List.of(
                    InlineKeyboardButton.builder()
                        .text("◀️ Назад")
                        .callbackData("strategies:menu")
                        .build()
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
