// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/DcaConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.DcaConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.DcaConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DcaConfigCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final DcaConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return update.getCallbackQuery().getData().startsWith("dca:");
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq      = update.getCallbackQuery();
        Long chatId           = cq.getMessage().getChatId();
        Integer messageId     = cq.getMessage().getMessageId();
        String data           = cq.getData();

        if ("dca:menu".equals(data)) return;

        AiTradeSettings settings = settingsService.getByChatId(chatId);
        DcaConfig cfg            = settings.getDcaConfig();

        switch (data) {
            case "dca:incOrders"   -> cfg.setOrderCount(cfg.getOrderCount() + 1);
            case "dca:decOrders"   -> cfg.setOrderCount(Math.max(1, cfg.getOrderCount() - 1));
            case "dca:incAmount"   -> cfg.setAmountPerOrder(
                                          cfg.getAmountPerOrder().add(BigDecimal.ONE)
                                      );
            case "dca:decAmount"   -> cfg.setAmountPerOrder(
                                          cfg.getAmountPerOrder().subtract(BigDecimal.ONE)
                                             .max(BigDecimal.ZERO)
                                      );
            case "dca:reset"       -> {
                var def = menuBuilder.getDefaultConfig();
                cfg.setOrderCount(def.getOrderCount());
                cfg.setAmountPerOrder(def.getAmountPerOrder());
            }
            default                 -> { return; }
        }

        settingsService.save(settings);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
