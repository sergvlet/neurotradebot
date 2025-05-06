// src/main/java/com/chicu/neurotradebot/telegram/handler/PairsImportCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.exchange.binance.BinanceClientProvider;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Импорт пар из Binance (берём первые 10 символов из exchangeInfo)
 */
@Component
@RequiredArgsConstructor
public class PairsImportCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final BinanceClientProvider clientProvider;
    private final AiTradeMenuBuilder aiBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && "pairs_import".equals(u.getCallbackQuery().getData());
    }

    @Override
    @Transactional
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        Long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();

        // Подтверждаем callback и ставим контекст
        sender.execute(new AnswerCallbackQuery(cq.getId()));
        BotContext.setChatId(chat);

        try {
            var user     = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);

            // Получаем exchangeInfo JSON
            String infoJson = clientProvider
                    .getClientForUser(user.getId())
                    .getExchangeInfo();

            // Выдираем первые 10 символов
            List<String> symbols = infoJson.lines()
                .filter(l -> l.contains("\"symbol\":"))
                .map(l -> l.replaceAll(".*\"symbol\"\\s*:\\s*\"([A-Z0-9]+)\".*", "$1"))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

            // Сбрасываем старые пары и добавляем новые
            settings.getPairs().clear();
            settings.getPairs().addAll(symbols);
            settings.setApiSetupStep(ApiSetupStep.NONE);
            settingsService.save(settings);

            // Формируем итоговый текст
            String text = "🔗 Импортировано пар:\n" +
                symbols.stream().collect(Collectors.joining(" / "));

            // Редактируем текущее сообщение, чтобы не спамить
            sender.execute(
                EditMessageText.builder()
                    .chatId(chat.toString())
                    .messageId(msgId)
                    .text(text)
                    .replyMarkup(aiBuilder.markup(chat))
                    .build()
            );

        } finally {
            BotContext.clear();
        }
    }
}
