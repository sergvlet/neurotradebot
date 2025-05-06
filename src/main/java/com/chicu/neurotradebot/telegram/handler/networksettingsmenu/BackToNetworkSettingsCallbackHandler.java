package com.chicu.neurotradebot.telegram.handler.networksettingsmenu;

import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.networksettingsmenu.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackToNetworkSettingsCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final NetworkSettingsViewBuilder viewBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
                && "back_to_settings".equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        BotContext.setChatId(chatId);
        try {
            // Получаем текущие настройки (режим testMode уже в них хранится)
            settingsService.getOrCreate(userService.getOrCreate(chatId));

            // Поскольку в текущей версии нет разделения AI/Manual, всегда считаем manual
            boolean fromAi = false;

            // Отправляем новое сообщение с меню сети
            sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(viewBuilder.title())
                    .replyMarkup(viewBuilder.markup(chatId, fromAi))
                    .build()
            );
        } catch (Exception e) {
            log.error("Ошибка при возврате в меню сетевых настроек", e);
        } finally {
            BotContext.clear();
        }
    }
}
