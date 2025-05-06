// src/main/java/com/chicu/neurotradebot/telegram/handler/BacktestCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.backtest.BacktestResult;
import com.chicu.neurotradebot.backtest.BacktestService;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BacktestCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final BacktestService backtestService;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && "ai_backtest".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq    = u.getCallbackQuery();
        long chat = cq.getMessage().getChatId();
        int  msgId= cq.getMessage().getMessageId();

        // 1) Подтверждаем callback без спама
        sender.execute(new AnswerCallbackQuery(cq.getId()));
        BotContext.setChatId(chat);

        try {
            AiTradeSettings cfg = settingsService.getOrCreate(userService.getOrCreate(chat));
            // 2) Запускаем бэктест
            BacktestResult result = backtestService.runBacktest(cfg);

            // 3) Формируем текст отчёта
            String text = String.format(
                "🧪 Backtest завершён\n" +
                "Общая доходность: %s%%\n" +
                "Max drawdown: %s%%\n" +
                "Win rate: %.2f%%\n" +
                "Profit factor: %s\n" +
                "Avg PnL: %s%%\n" +
                "Avg duration: %.1f мин.",
                result.totalReturn, 
                result.maxDrawdown, 
                result.winRate * 100, 
                result.profitFactor, 
                result.averagePnL, 
                result.avgDurationMinutes
            );

            // 4) Редактируем оригинальное сообщение, чтобы не спамить
            sender.execute(EditMessageText.builder()
                .chatId(Long.toString(chat))
                .messageId(msgId)
                .text(text)
                .build()
            );
        } finally {
            BotContext.clear();
        }
    }
}
