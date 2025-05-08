package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
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
    private final RsiConfigMenuBuilder    menuBuilder;


    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        String data = update.getCallbackQuery().getData();
        return data != null && data.startsWith("rsi:");
    }

    @Override
    public void handle(Update update) throws Exception {
        CallbackQuery cq   = update.getCallbackQuery();
        Long chatId        = cq.getMessage().getChatId();
        Integer messageId  = cq.getMessage().getMessageId();
        String data        = cq.getData();

        // Получаем настройки и текущий RSI-конфиг
        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        RsiMacdConfig c     = cfg.getRsiMacdConfig();

        // Обрабатываем инк/дек и сброс
        switch (data) {
            case "rsi:incPeriod" -> 
                c.setRsiPeriod(Math.min(100, c.getRsiPeriod() + 1));
            case "rsi:decPeriod" -> 
                c.setRsiPeriod(Math.max(1, c.getRsiPeriod() - 1));
            case "rsi:incLower"  -> 
                c.setRsiLower(c.getRsiLower().add(BigDecimal.ONE).min(BigDecimal.valueOf(100)));
            case "rsi:decLower"  -> 
                c.setRsiLower(c.getRsiLower().subtract(BigDecimal.ONE).max(BigDecimal.ZERO));
            case "rsi:incUpper"  -> 
                c.setRsiUpper(c.getRsiUpper().add(BigDecimal.ONE).min(BigDecimal.valueOf(100)));
            case "rsi:decUpper"  -> 
                c.setRsiUpper(c.getRsiUpper().subtract(BigDecimal.ONE).max(BigDecimal.ZERO));
            case "rsi:reset"     -> 
                cfg.setRsiMacdConfig(menuBuilder.getDefaultConfig());
            case "rsi:menu"      -> {
                // просто перерисуем текущее меню
            }
            default -> {
                // ничего не делать
            }
        }

        // Сохраняем изменения и перерисовываем меню в том же сообщении
        settingsService.save(cfg);
        menuBuilder.buildOrEditMenu(chatId, messageId);
    }
}
