package com.chicu.neurotradebot.telegram.handler.aimenu.riskmenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RiskConfig;
import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.MessageHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.riskmenu.RiskMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.math.BigDecimal;

// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/riskmenu/RiskValueMessageHandler.java
@Component
@RequiredArgsConstructor
public class RiskValueMessageHandler implements MessageHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;
    private final RiskMenuBuilder riskBuilder;

    @Override
    public boolean canHandle(Update u) {
        if (!u.hasMessage() || u.getMessage().getText() == null) {
            return false;
        }
        long chat = u.getMessage().getChatId();
        AiTradeSettings cfg = settingsService.getOrCreate(userService.getOrCreate(chat));
        return switch (cfg.getApiSetupStep()) {
            case ENTER_RISK_SL, ENTER_RISK_TP, ENTER_RISK_MAXP ->
                u.getMessage().getText().trim().replace("%", "").matches("\\d+(\\.\\d+)?");
            default -> false;
        };
    }

    @Override
    public void handle(Update u) throws Exception {
        long chat    = u.getMessage().getChatId();
        int  userMsg = u.getMessage().getMessageId();
        String text  = u.getMessage().getText().trim().replace("%", "");


        try {
            AiTradeSettings cfg = settingsService.getOrCreate(userService.getOrCreate(chat));
            Integer promptId = cfg.getApiSetupPromptMsgId();

            if (cfg.getRiskConfig() == null) {
                cfg.setRiskConfig(new RiskConfig());
            }

            BigDecimal val = new BigDecimal(text);
            switch (cfg.getApiSetupStep()) {
                case ENTER_RISK_SL   -> cfg.getRiskConfig().setStopLossPercent(val);
                case ENTER_RISK_TP   -> cfg.getRiskConfig().setTakeProfitPercent(val);
                case ENTER_RISK_MAXP -> cfg.getRiskConfig().setMaxPercentPerTrade(val);
                default -> {}
            }

            cfg.setApiSetupStep(ApiSetupStep.NONE);
            cfg.setApiSetupPromptMsgId(null);
            settingsService.save(cfg);

            // удаляем сообщение пользователя
            try {
                sender.execute(DeleteMessage.builder()
                    .chatId(Long.toString(chat))
                    .messageId(userMsg)
                    .build()
                );
            } catch (TelegramApiRequestException ignored) {}

            // редактируем исходное сообщение меню риска
            if (promptId != null) {
                sender.execute(EditMessageText.builder()
                    .chatId(Long.toString(chat))
                    .messageId(promptId)
                    .text(riskBuilder.title())
                    .replyMarkup(riskBuilder.markup(chat))
                    .build()
                );
            }
        } finally {
            BotContext.clear();
        }
    }
}
