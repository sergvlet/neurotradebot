package com.chicu.neurotradebot.telegram.config;

import com.chicu.neurotradebot.telegram.core.CryptoTradeTelegramBot;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotInitializer {

    private final CryptoTradeTelegramBot bot;

    public TelegramBotInitializer(CryptoTradeTelegramBot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось зарегистрировать Telegram-бота", e);
        }
    }
}
