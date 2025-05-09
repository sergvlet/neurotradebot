// src/main/java/com/chicu/neurotradebot/telegram/handler/networksettingsmenu/ToggleModeCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.networksettingsmenu;


import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
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
public class ToggleModeCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final NetworkSettingsViewBuilder viewBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
                && "toggle_mode".equals(update.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq    = update.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        Integer msgId = cq.getMessage().getMessageId();

        try {
            // 1) Переключаем testMode
            User user = userService.getOrCreate(chat);
            var s = settingsService.getOrCreate(user);
            s.setTestMode(!s.isTestMode());
            settingsService.save(s);

            // 2) Ответ на callback и правка меню
            sender.execute(new AnswerCallbackQuery(cq.getId()));

            sender.execute(
                    EditMessageText.builder()
                            .chatId(chat.toString())
                            .messageId(msgId)
                            .text(viewBuilder.title())
                            .replyMarkup(viewBuilder.markup(chat))  // <-- только chatId
                            .build()
            );

            log.info("Переключили режим на {} для chatId={}",
                    s.isTestMode() ? "TESTNET" : "REAL", chat);
        } finally {
            BotContext.clear();
        }
    }
}
