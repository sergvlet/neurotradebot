package com.chicu.neurotradebot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSender {
    private final ApplicationContext ctx;

    /**
     * Выполняет любой метод Telegram API.
     * Если приходит ошибка "message is not modified" — тихо её игнорируем.
     */
    public <T extends Serializable> T execute(BotApiMethod<T> method) throws TelegramApiException {
        try {
            NeuroTradeBot bot = ctx.getBean(NeuroTradeBot.class);
            return bot.execute(method);
        } catch (TelegramApiRequestException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("message is not modified")) {
                log.debug("Игнорирую попытку отредактировать сообщение без изменений");
                return null;
            }
            // все остальные ошибки — пробрасываем дальше
            throw e;
        }
    }
}
