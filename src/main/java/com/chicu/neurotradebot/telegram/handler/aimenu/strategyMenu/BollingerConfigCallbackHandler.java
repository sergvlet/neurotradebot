// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/BollingerConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.BollingerConfig;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.BollingerConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BollingerConfigCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final BollingerConfigMenuBuilder menuBuilder;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return update.getCallbackQuery().getData().startsWith("bollinger:");
    }

    @Override
    public void handle(Update update) {
        CallbackQuery cq      = update.getCallbackQuery();
        Long chatId           = cq.getMessage().getChatId();
        Integer messageId     = cq.getMessage().getMessageId();
        String data           = cq.getData();

        if ("bollinger:menu".equals(data)) return;

        AiTradeSettings settings = settingsService.getByChatId(chatId);
        BollingerConfig cfg      = settings.getBollingerConfig();

        switch (data) {
            case "bollinger:incPeriod"    -> cfg.setPeriod(cfg.getPeriod() + 1);
            case "bollinger:decPeriod"    -> cfg.setPeriod(Math.max(1, cfg.getPeriod() - 1));
            case "bollinger:incMultiplier"-> cfg.setStdDevMultiplier(cfg.getStdDevMultiplier().add(BigDecimal.ONE));
            case "bollinger:decMultiplier"-> cfg.setStdDevMultiplier(
                                               cfg.getStdDevMultiplier().subtract(BigDecimal.ONE)
                                                  .max(BigDecimal.ZERO)
                                             );
            case "bollinger:reset"        -> {
                var def = menuBuilder.getDefaultConfig();
                cfg.setPeriod(def.getPeriod());
                cfg.setStdDevMultiplier(def.getStdDevMultiplier());
            }
            default                       -> { return; }
        }

        settingsService.save(settings);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
