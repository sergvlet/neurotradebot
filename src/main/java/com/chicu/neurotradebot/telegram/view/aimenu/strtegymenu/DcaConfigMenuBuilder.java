// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/DcaConfigMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.DcaConfig;
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
public class DcaConfigMenuBuilder {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;

    // дефолтные параметры DCA из сущности
    private static final DcaConfig DEFAULT = DcaConfig.builder().build();

    public DcaConfig getDefaultConfig() {
        return DEFAULT;
    }

    public void buildOrEditMenu(Long chatId, Integer messageId) {
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        DcaConfig cfg = settings.getDcaConfig();
        if (cfg == null) {
            DcaConfig def = new DcaConfig();
            def.setSettings(settings);
            settings.setDcaConfig(def);
            settingsService.save(settings);
            cfg = def;
        }

        boolean isDefault =
            cfg.getOrderCount() == DEFAULT.getOrderCount() &&
            cfg.getAmountPerOrder().compareTo(DEFAULT.getAmountPerOrder()) == 0;

        StringBuilder text = new StringBuilder();
        text.append(isDefault
            ? "*Дефолтные* настройки DCA\n"
            : "*Пользовательские* настройки DCA\n");
        text.append("• orders: ").append(cfg.getOrderCount()).append("\n");
        text.append("• amount: ").append(cfg.getAmountPerOrder().toPlainString()).append("\n\n");

        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(
                    InlineKeyboardButton.builder().text("– orders").callbackData("dca:decOrders").build(),
                    InlineKeyboardButton.builder().text("orders").callbackData("dca:menu").build(),
                    InlineKeyboardButton.builder().text("+ orders").callbackData("dca:incOrders").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("– amount").callbackData("dca:decAmount").build(),
                    InlineKeyboardButton.builder().text("amount").callbackData("dca:menu").build(),
                    InlineKeyboardButton.builder().text("+ amount").callbackData("dca:incAmount").build()
                ),
                List.of(
                    InlineKeyboardButton.builder().text("🔄 сбросить дефол").callbackData("dca:reset").build()
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
