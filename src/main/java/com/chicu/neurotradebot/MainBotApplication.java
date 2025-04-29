package com.chicu.neurotradebot;

import com.chicu.neurotradebot.telegram.bot.NeuroTradeBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@RequiredArgsConstructor
public class MainBotApplication {

    private final NeuroTradeBot neuroTradeBot;

    public static void main(String[] args) {
        SpringApplication.run(MainBotApplication.class, args);
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(neuroTradeBot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
