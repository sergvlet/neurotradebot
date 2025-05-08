// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/EmaConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.EmaCrossoverConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.EmaConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class EmaConfigCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final EmaConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        String data = update.getCallbackQuery().getData();
        return data.startsWith("ema:");
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq       = update.getCallbackQuery();
        Long chatId            = cq.getMessage().getChatId();
        Integer messageId      = cq.getMessage().getMessageId();
        String data            = cq.getData();

        if ("ema:menu".equals(data)) return;

        AiTradeSettings settings = settingsService.getByChatId(chatId);
        EmaCrossoverConfig cfg   = settings.getEmaCrossoverConfig();

        switch (data) {
            case "ema:incShort" -> cfg.setShortPeriod(cfg.getShortPeriod() + 1);
            case "ema:decShort" -> cfg.setShortPeriod(Math.max(1, cfg.getShortPeriod() - 1));
            case "ema:incLong"  -> cfg.setLongPeriod(cfg.getLongPeriod() + 1);
            case "ema:decLong"  -> cfg.setLongPeriod(Math.max(1, cfg.getLongPeriod() - 1));
            case "ema:reset"    -> {
                var def = menuBuilder.getDefaultConfig();
                cfg.setShortPeriod(def.getShortPeriod());
                cfg.setLongPeriod(def.getLongPeriod());
            }
            default              -> { return; }
        }

        settingsService.save(settings);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
