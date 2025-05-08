// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/MacdConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
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
        String data = update.getCallbackQuery().getData();
        return data.startsWith("macd:");
    }

    @Override
    public void handle(Update update) throws Exception {
        CallbackQuery cq = update.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        Integer messageId = cq.getMessage().getMessageId();
        String data = cq.getData();

        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        RsiMacdConfig c   = cfg.getRsiMacdConfig();

        switch (data) {
            case "macd:incFast"   -> c.setMacdFast(c.getMacdFast() + 1);
            case "macd:decFast"   -> c.setMacdFast(Math.max(1, c.getMacdFast() - 1));
            case "macd:incSlow"   -> c.setMacdSlow(c.getMacdSlow() + 1);
            case "macd:decSlow"   -> c.setMacdSlow(Math.max(1, c.getMacdSlow() - 1));
            case "macd:incSignal" -> c.setMacdSignal(c.getMacdSignal() + 1);
            case "macd:decSignal" -> c.setMacdSignal(Math.max(1, c.getMacdSignal() - 1));
            case "macd:reset"     -> cfg.setRsiMacdConfig(menuBuilder.getDefaultConfig());
            case "macd:menu"      -> {
                // noop: просто перерисуем текущее меню
            }
            default               -> {
                // для всех прочих данных — ничего не делать
                return;
            }
        }

        // Сохраняем обновлённую конфигурацию и перерисовываем меню в том же сообщении
        settingsService.save(cfg);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
