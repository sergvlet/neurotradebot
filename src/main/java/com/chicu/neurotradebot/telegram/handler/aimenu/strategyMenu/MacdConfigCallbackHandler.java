// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/MacdConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.MacdConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.MacdConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class MacdConfigCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final MacdConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return update.getCallbackQuery().getData().startsWith("macd:");
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq   = update.getCallbackQuery();
        Long chatId        = cq.getMessage().getChatId();
        Integer messageId  = cq.getMessage().getMessageId();
        String data        = cq.getData();

        // Если это просто запрос перерисовать меню без изменений — выходим
        if ("macd:menu".equals(data)) {
            return;
        }

        AiTradeSettings settings = settingsService.getByChatId(chatId);
        MacdConfig cfg           = settings.getMacdConfig();

        switch (data) {
            case "macd:incFast"   -> cfg.setFast(cfg.getFast() + 1);
            case "macd:decFast"   -> cfg.setFast(Math.max(1, cfg.getFast() - 1));
            case "macd:incSlow"   -> cfg.setSlow(cfg.getSlow() + 1);
            case "macd:decSlow"   -> cfg.setSlow(Math.max(1, cfg.getSlow() - 1));
            case "macd:incSignal" -> cfg.setSignal(cfg.getSignal() + 1);
            case "macd:decSignal" -> cfg.setSignal(Math.max(1, cfg.getSignal() - 1));
            case "macd:reset"     -> {
                var def = menuBuilder.getDefaultConfig();
                cfg.setFast(def.getFast());
                cfg.setSlow(def.getSlow());
                cfg.setSignal(def.getSignal());
            }
            default                -> { return; }
        }

        settingsService.save(settings);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
