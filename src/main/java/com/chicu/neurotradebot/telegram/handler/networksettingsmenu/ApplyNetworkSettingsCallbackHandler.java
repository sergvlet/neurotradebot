// src/main/java/com/chicu/neurotradebot/telegram/handler/ApplyNetworkSettingsCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.networksettingsmenu;

import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import com.chicu.neurotradebot.telegram.view.manualtredemenu.ManualTradeMenuBuilder;
import com.chicu.neurotradebot.telegram.view.networksettingsmenu.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplyNetworkSettingsCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final NetworkSettingsViewBuilder networkBuilder;
    private final AiTradeMenuBuilder aiBuilder;
    private final ManualTradeMenuBuilder manualBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;
        String data = update.getCallbackQuery().getData();
        return "apply_network_settings_ai".equals(data)
            || "apply_network_settings_manual".equals(data);
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq    = update.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();

        // откуда пришли — поймём по callbackData
        boolean fromAi = "apply_network_settings_ai".equals(cq.getData());

        BotContext.setChatId(chat);
        try {
            // сняли spinner
            sender.execute(new AnswerCallbackQuery(cq.getId()));

            // редактируем текст на нужное меню
            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(fromAi
                    ? aiBuilder.title()
                    : manualBuilder.title())
                .replyMarkup(fromAi
                    ? aiBuilder.markup(chat)
                    : manualBuilder.markup(chat))
                .build();

            sender.execute(edit);
            log.info("Перешли из сет.настроек в {}-меню for chatId={}",
                fromAi ? "AI" : "Manual", chat);
        } finally {
            BotContext.clear();
        }
    }
}
