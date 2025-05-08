// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/StrategySelectCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.StrategyMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StrategySelectCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final StrategyMenuBuilder menuBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        if (!u.hasCallbackQuery()) return false;
        String d = u.getCallbackQuery().getData();
        return d.startsWith("strat_toggle_") || d.equals("strat_done");
    }

    @Override
    @Transactional
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();
        String data = cq.getData();

        sender.execute(new AnswerCallbackQuery(cq.getId()));

        var user = userService.getOrCreate(chat);
        var cfg  = settingsService.getOrCreate(user);

        if (data.equals("strat_done")) {
            // закрываем меню и показываем главное
        } else {
            String name = data.substring("strat_toggle_".length());
            StrategyType t = StrategyType.valueOf(name);
            if (cfg.getStrategies().contains(t)) {
                cfg.getStrategies().remove(t);
            } else {
                cfg.getStrategies().add(t);
            }
            settingsService.save(cfg);
        }

        var text   = menuBuilder.title();
        var markup = menuBuilder.markup(chat);

        sender.execute(EditMessageText.builder()
            .chatId(Long.toString(chat))
            .messageId(msgId)
            .text(text)
            .replyMarkup(markup)
            .build());
        BotContext.clear();
    }
}
