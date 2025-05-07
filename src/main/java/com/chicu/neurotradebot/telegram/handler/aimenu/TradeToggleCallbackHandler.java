// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/TradeToggleCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.TradingTaskManager;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TradeToggleCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final TradingTaskManager     taskManager;
    private final AiTradeMenuBuilder      menuBuilder;
    private final TelegramSender          sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
                && "ai_trade_toggle".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq      = u.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        Integer msg = cq.getMessage().getMessageId();

        // Загрузка настроек по chatId
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        // Переключаем флаг
        boolean newState = !settings.isEnabled();
        settings.setEnabled(newState);
        settingsService.save(settings);

        // Планируем или отменяем задачу
        if (newState) {
            taskManager.scheduleFor(chatId);
        } else {
            taskManager.cancelFor(chatId);
        }

        // Обновляем меню: статус + кнопки
        String status = newState ? "✅ Торговля запущена" : "⚠️ Торговля остановлена";
        String title  = menuBuilder.title();
        var kb = menuBuilder.markup(chatId);

        sender.execute(
                EditMessageText.builder()
                        .chatId(chatId.toString())
                        .messageId(msg)
                        .text(status + "\n\n" + title)
                        .replyMarkup(kb)
                        .build()
        );
    }
}
