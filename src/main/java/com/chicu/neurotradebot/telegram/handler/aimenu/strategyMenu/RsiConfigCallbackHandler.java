// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/RsiConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.RsiConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RsiConfigCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final RsiConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return update.getCallbackQuery().getData().startsWith("rsi:");
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq       = update.getCallbackQuery();
        Long chatId            = cq.getMessage().getChatId();
        Integer messageId      = cq.getMessage().getMessageId();
        String data            = cq.getData();

        // если это просто "rsi:menu" — не вызываем editMessage, чтобы избежать "message not modified"
        if ("rsi:menu".equals(data)) {
            return;
        }

        AiTradeSettings settings = settingsService.getByChatId(chatId);
        RsiConfig cfg            = settings.getRsiConfig();

        switch (data) {
            case "rsi:incPeriod" -> cfg.setPeriod(cfg.getPeriod() + 1);
            case "rsi:decPeriod" -> cfg.setPeriod(Math.max(1, cfg.getPeriod() - 1));
            case "rsi:incLower"  -> cfg.setLower(cfg.getLower().add(BigDecimal.ONE));
            case "rsi:decLower"  -> cfg.setLower(cfg.getLower().subtract(BigDecimal.ONE).max(BigDecimal.ZERO));
            case "rsi:incUpper"  -> cfg.setUpper(cfg.getUpper().add(BigDecimal.ONE));
            case "rsi:decUpper"  -> cfg.setUpper(cfg.getUpper().subtract(BigDecimal.ONE).max(BigDecimal.ZERO));
            case "rsi:reset"     -> {
                var def = menuBuilder.getDefaultConfig();
                cfg.setPeriod(def.getPeriod());
                cfg.setLower(def.getLower());
                cfg.setUpper(def.getUpper());
            }
            default               -> { return; }
        }

        settingsService.save(settings);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
