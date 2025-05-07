// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/riskmenu/RiskMaxPercentCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.riskmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.riskmenu.RiskMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RiskMaxPercentCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final RiskMenuBuilder riskBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
            && "risk_mp".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq    = u.getCallbackQuery();
        long chat = cq.getMessage().getChatId();
        int msgId = cq.getMessage().getMessageId();

        // 1) Подтверждаем callback без спама
        sender.execute(new AnswerCallbackQuery(cq.getId()));

        // 2) Устанавливаем шаг и сохраняем ID сообщения, которое будем потом редактировать/удалять
        
        var cfg = settingsService.getOrCreate(userService.getOrCreate(chat));
        cfg.setApiSetupStep(ApiSetupStep.ENTER_RISK_MAXP);
        cfg.setApiSetupPromptMsgId(msgId);
        settingsService.save(cfg);

        // 3) Правим текущее сообщение: показываем приглашение и кнопку «Отмена»
        sender.execute(
            EditMessageText.builder()
                .chatId(Long.toString(chat))
                .messageId(msgId)
                .text("💰 Введите Max % per trade (например, 2.5):")
                .replyMarkup(riskBuilder.cancelMarkup(chat))
                .build()
        );

        BotContext.clear();
    }
}
