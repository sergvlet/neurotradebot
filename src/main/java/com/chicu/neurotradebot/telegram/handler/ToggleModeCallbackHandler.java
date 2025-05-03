package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.view.NetworkSettingsMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToggleModeCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final NetworkSettingsMenuBuilder menuBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(CallbackQuery callbackQuery) {
        return "toggle_mode".equals(callbackQuery.getData());
    }

    @Override
    public void handle(CallbackQuery callbackQuery) throws Exception {
        Long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        BotContext.setChatId(chatId);

        try {
            User user = userService.getOrCreate(chatId);
            var settings = settingsService.getOrCreate(user);

            boolean currentMode = settings.isTestMode();
            settings.setTestMode(!currentMode);
            settingsService.save(settings);

            sender.execute(
                EditMessageReplyMarkup.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .replyMarkup(menuBuilder.buildNetworkSettingsMenu(!currentMode))
                    .build()
            );

            log.info("Режим торговли переключён: {} → {} для chatId={}", currentMode, !currentMode, chatId);
        } finally {
            BotContext.clear();
        }
    }
}
