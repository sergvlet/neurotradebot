package com.chicu.neurotradebot.telegram.handler.main;

import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import com.chicu.neurotradebot.telegram.handler.menu.SubscriptionMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class MainMenuHandler {

    private final StartMenuBuilder startMenuBuilder;
    private final SubscriptionMenuBuilder subscriptionMenuBuilder;

    public Object startNewMessage(Message message) {
        long chatId = message.getChatId();

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("""
                      👋 Привет!

                      Добро пожаловать в NeuroTradeBot.

                      Выберите действие в меню ниже:
                      """)
                .replyMarkup(startMenuBuilder.buildMainMenu())
                .parseMode("Markdown")
                .build();
    }

    public Object editStartMenu(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                      👋 Привет!

                      Добро пожаловать в NeuroTradeBot.

                      Выберите действие в меню ниже:
                      """)
                .replyMarkup(startMenuBuilder.buildMainMenu())
                .parseMode("Markdown")
                .build();
    }

    public Object showSubscriptionMenu(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                      🔥 Выберите тарифный план:

                      ▫️ 10 дней триал (бесплатно)
                      ▫️ 1 месяц
                      ▫️ 3 месяца
                      ▫️ 6 месяцев
                      ▫️ 12 месяцев
                      """)
                .replyMarkup(subscriptionMenuBuilder.buildSubscriptionMenu())
                .parseMode("Markdown")
                .build();
    }
}
