// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/StrategySelectCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.aimenu.StrategyMenuBuilder;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StrategySelectCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final StrategyMenuBuilder    menuBuilder;
    private final TelegramSender         sender;

    public StrategySelectCallbackHandler(AiTradeSettingsService settingsService,
                                         StrategyMenuBuilder menuBuilder,
                                         TelegramSender sender) {
        this.settingsService = settingsService;
        this.menuBuilder     = menuBuilder;
        this.sender          = sender;
    }

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() &&
               u.getCallbackQuery().getData().startsWith("toggle_strat_");
    }

    @Override
    @Transactional
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        Long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();
        String data = cq.getData();                 // e.g. "toggle_strat_RSI"

        sender.execute(new AnswerCallbackQuery(cq.getId()));

        String name = data.substring("toggle_strat_".length());
        StrategyType strat = StrategyType.valueOf(name);

        AiTradeSettings cfg = settingsService.getByChatId(chat);
        if (cfg.getStrategies().contains(strat)) {
            cfg.getStrategies().remove(strat);
        } else {
            cfg.getStrategies().add(strat);
        }
        settingsService.save(cfg);

        // Редактируем текущее сообщение с обновлёнными «чекбоксами»
        sender.execute(EditMessageText.builder()
            .chatId(chat.toString())
            .messageId(msgId)
            .text(menuBuilder.title())
            .replyMarkup(menuBuilder.markup(chat))
            .build()
        );

        BotContext.clear();
    }
}
