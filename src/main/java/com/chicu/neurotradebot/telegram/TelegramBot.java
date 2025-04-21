package com.chicu.neurotradebot.telegram;

import com.chicu.neurotradebot.telegram.callback.BotCallback;
import com.chicu.neurotradebot.telegram.callback.CallbackFactory;
import com.chicu.neurotradebot.telegram.callback.CallbackProcessor;
import com.chicu.neurotradebot.telegram.callback.TradeLimitInputProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties properties;
    private final CallbackFactory callbackFactory;
    private final TradeLimitInputProcessor tradeLimitInputProcessor;

    @PostConstruct
    public void init() {
        log.info("ℹ️ Webhook не установлен, всё чисто");
        log.info("✅ Бот работает в режиме Long Polling");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();

            // Если бот ждёт ввода лимита сделки — обрабатываем
            tradeLimitInputProcessor.process(message, this);

            // Стартовая команда
            if (text.equals("/start")) {
                CallbackProcessor processor = callbackFactory.getProcessor(BotCallback.MAIN_MENU);
                processor.process(chatId, null, BotCallback.MAIN_MENU.getValue(), this);
            }
        }

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData(); // пример: "TOGGLE_STRATEGY:SMA"
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            String callbackPrefix = data.split(":")[0];
            BotCallback callback = BotCallback.fromValue(callbackPrefix);

            CallbackProcessor processor = callbackFactory.getProcessor(callback);
            if (processor != null) {
                processor.process(chatId, messageId, data, this);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }
}
