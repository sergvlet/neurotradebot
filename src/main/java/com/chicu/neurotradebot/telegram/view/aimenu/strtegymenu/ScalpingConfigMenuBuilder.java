// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/ScalpingConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.ScalpingConfig;
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
public class ScalpingConfigMenuBuilder {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;

    // дефолтные параметры Scalping из сущности
    private static final ScalpingConfig DEFAULT = ScalpingConfig.builder().build();

    public ScalpingConfig getDefaultConfig() {
        return DEFAULT;
    }

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        ScalpingConfig cfg = settings.getScalpingConfig();
        if (cfg == null) {
            ScalpingConfig def = new ScalpingConfig();
            def.setSettings(settings);
            settings.setScalpingConfig(def);
            settingsService.save(settings);
            cfg = def;
        }

        boolean isDefault =
            cfg.getOrderBookDepth() == DEFAULT.getOrderBookDepth() &&
            cfg.getProfitThreshold().compareTo(DEFAULT.getProfitThreshold()) == 0 &&
            cfg.getStopLossThreshold().compareTo(DEFAULT.getStopLossThreshold()) == 0;

        StringBuilder text = new StringBuilder();
        text.append(isDefault
            ? "*Дефолтные* настройки Scalping\n"
            : "*Пользовательские* настройки Scalping\n");
        text.append("• depth: ").append(cfg.getOrderBookDepth()).append("\n");
        text.append("• profit%: ").append(cfg.getProfitThreshold().toPlainString()).append("\n");
        text.append("• stopLoss%: ").append(cfg.getStopLossThreshold().toPlainString()).append("\n\n");

        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder().text("– depth").callbackData("scalp:decDepth").build(),
                    InlineKeyboardButton.builder().text("depth").callbackData("scalp:menu").build(),
                    InlineKeyboardButton.builder().text("+ depth").callbackData("scalp:incDepth").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("– profit%").callbackData("scalp:decProfit").build(),
                    InlineKeyboardButton.builder().text("profit%").callbackData("scalp:menu").build(),
                    InlineKeyboardButton.builder().text("+ profit%").callbackData("scalp:incProfit").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("– stopLoss%").callbackData("scalp:decStopLoss").build(),
                    InlineKeyboardButton.builder().text("stopLoss%").callbackData("scalp:menu").build(),
                    InlineKeyboardButton.builder().text("+ stopLoss%").callbackData("scalp:incStopLoss").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("🔄 сбросить дефол").callbackData("scalp:reset").build()
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
