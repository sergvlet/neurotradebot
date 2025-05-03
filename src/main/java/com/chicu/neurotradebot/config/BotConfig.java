// src/main/java/com/chicu/neurotradebot/config/BotConfig.java
package com.chicu.neurotradebot.config;

import com.chicu.neurotradebot.telegram.NeuroTradeBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class BotConfig {

    /**
     * Создаёт TelegramBotsApi и регистрирует в нём ваш бот.
     * Без этого регистрация не произойдёт, и onUpdateReceived() не вызовется.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(NeuroTradeBot neuroTradeBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(neuroTradeBot);
        return botsApi;
    }
}
