// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/ScalpingConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.ScalpingConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.ScalpingConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ScalpingConfigCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final ScalpingConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return update.getCallbackQuery().getData().startsWith("scalp:");
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq      = update.getCallbackQuery();
        Long chatId           = cq.getMessage().getChatId();
        Integer messageId     = cq.getMessage().getMessageId();
        String data           = cq.getData();

        if ("scalp:menu".equals(data)) return;

        AiTradeSettings settings = settingsService.getByChatId(chatId);
        ScalpingConfig cfg       = settings.getScalpingConfig();

        switch (data) {
            case "scalp:incDepth"    -> cfg.setOrderBookDepth(cfg.getOrderBookDepth() + 1);
            case "scalp:decDepth"    -> cfg.setOrderBookDepth(Math.max(1, cfg.getOrderBookDepth() - 1));
            case "scalp:incProfit"   -> cfg.setProfitThreshold(cfg.getProfitThreshold().add(BigDecimal.ONE));
            case "scalp:decProfit"   -> cfg.setProfitThreshold(cfg.getProfitThreshold().subtract(BigDecimal.ONE).max(BigDecimal.ZERO));
            case "scalp:incStopLoss" -> cfg.setStopLossThreshold(cfg.getStopLossThreshold().add(BigDecimal.ONE));
            case "scalp:decStopLoss" -> cfg.setStopLossThreshold(cfg.getStopLossThreshold().subtract(BigDecimal.ONE).max(BigDecimal.ZERO));
            case "scalp:reset"       -> {
                var def = menuBuilder.getDefaultConfig();
                cfg.setOrderBookDepth(def.getOrderBookDepth());
                cfg.setProfitThreshold(def.getProfitThreshold());
                cfg.setStopLossThreshold(def.getStopLossThreshold());
            }
            default                  -> { return; }
        }

        settingsService.save(settings);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
