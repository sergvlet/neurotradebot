package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.view.ExchangeMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
@Slf4j
public class SelectExchangeCallbackHandler implements CallbackHandler {

    private final TelegramSender sender;
    private final ExchangeMenuBuilder exchangeMenuBuilder;

    @Override
    public boolean canHandle(CallbackQuery callbackQuery) {
        return "select_exchange".equals(callbackQuery.getData());
    }

    @Override
    public void handle(CallbackQuery callbackQuery) throws Exception {
        Long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        BotContext.setChatId(chatId);

        try {
            sender.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text("Выберите биржу:")
                    .replyMarkup(exchangeMenuBuilder.buildExchangeSelectionMenu())
                    .build()
            );

            log.info("Открыто меню выбора биржи для chatId={}", chatId);
        } finally {
            BotContext.clear();
        }
    }
}
