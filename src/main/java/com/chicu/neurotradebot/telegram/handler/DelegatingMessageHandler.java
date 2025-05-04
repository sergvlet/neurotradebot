// src/main/java/com/chicu/neurotradebot/telegram/handler/DelegatingMessageHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Делегирует текстовые сообщения в MenuDefinition, которые реализуют TextInputAwareMenu.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DelegatingMessageHandler implements MessageHandler {

    private final List<MenuDefinition> menus;
    private final UserService userService;
    private final AiTradeSettingsService settingsService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;

        Long chatId = update.getMessage().getChatId();
        User user = userService.getOrCreate(chatId);
        var settings = settingsService.getOrCreate(user);

        // здесь можно дополнить более точной логикой ожидания текстового ввода
        return settings.getExchange() != null;
    }

    @Override
    public void handle(Update update) throws Exception {
        for (MenuDefinition menu : menus) {
            if (menu instanceof TextInputAwareMenu handler) {
                handler.handleText(update);
                log.info("✉️ Сообщение делегировано в {}", menu.getClass().getSimpleName());
                return;
            }
        }

        log.warn("❌ Нет меню, поддерживающего текстовый ввод");
    }
}
