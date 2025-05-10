// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/MlTpSlConfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.enums.ConfigWaiting;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.MlTpSlConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class MlTpSlConfigCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;
    private final MlTpSlConfigMenuBuilder menu;

    @Override
    public boolean canHandle(Update upd) {
        if (!upd.hasCallbackQuery()) return false;
        String d = upd.getCallbackQuery().getData();
        return d.startsWith("ml:");
    }

    @Override
    public void handle(Update upd) {
        CallbackQuery cq = upd.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        Integer msgId = cq.getMessage().getMessageId();
        String d = cq.getData();

        switch (d) {
            case "ml:menu":
                menu.buildOrEditMenu(chatId, msgId);
                break;

            case "ml:incCapital":
                settingsService.updateMlTotalCapital(chatId, +10);
                menu.buildOrEditMenu(chatId, msgId);
                break;
            case "ml:decCapital":
                settingsService.updateMlTotalCapital(chatId, -10);
                menu.buildOrEditMenu(chatId, msgId);
                break;

            case "ml:incRsi":
                settingsService.updateMlEntryRsiThreshold(chatId, +1.0);
                menu.buildOrEditMenu(chatId, msgId);
                break;
            case "ml:decRsi":
                settingsService.updateMlEntryRsiThreshold(chatId, -1.0);
                menu.buildOrEditMenu(chatId, msgId);
                break;

            case "ml:incLookback":
                settingsService.updateMlLookbackPeriod(chatId, +1);
                menu.buildOrEditMenu(chatId, msgId);
                break;
            case "ml:decLookback":
                settingsService.updateMlLookbackPeriod(chatId, -1);
                menu.buildOrEditMenu(chatId, msgId);
                break;

            case "ml:setUrl":
                // здесь используем ConfigWaiting для ожидания ввода URL
                settingsService.markWaiting(chatId, ConfigWaiting.AI_ML_SET_URL);
                sender.sendMessage(chatId,
                    "Введите новый Predict URL, например http://localhost:5000/predict");
                break;

            case "ml:reset":
                settingsService.resetMlConfig(chatId);
                menu.buildOrEditMenu(chatId, msgId);
                break;

            default:
                // игнорируем
                break;
        }
    }
}
