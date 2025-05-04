// src/main/java/com/chicu/neurotradebot/telegram/handler/StartMenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.view.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartMenuDefinition implements MenuDefinition, MessageHandler {

    private final StartMenuBuilder builder;
    private final TelegramSender sender;

    @Override
    public Set<String> keys() {
        return Set.of("/start", "back_to_main");
    }

    @Override
    public String title() {
        return "🏠 Главное меню:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        return builder.buildMainMenu();
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
                && "/start".equals(update.getMessage().getText());
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        BotContext.setChatId(chatId);

        try {
            sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(title())
                    .replyMarkup(markup(chatId))
                    .build());
            log.info("✅ Главное меню отправлено для chatId={}", chatId);
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке главного меню", e);
        } finally {
            BotContext.clear();
        }
    }
}
