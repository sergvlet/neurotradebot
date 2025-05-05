package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.NetworkSettingsViewBuilder;
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
        var cq = update.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        int msgId = cq.getMessage().getMessageId();
        BotContext.setChatId(chatId);

        try {
            // 1) переключаем testMode в настройках
            User user = userService.getOrCreate(chatId);
            var s = settingsService.getOrCreate(user);
            s.setTestMode(!s.isTestMode());
            settingsService.save(s);

            // 2) отвечаем на callback и редактируем текущее сообщение
            sender.execute(new AnswerCallbackQuery(cq.getId()));

            // пока считаем, что меню вызвано из ручного режима
            boolean fromAi = false;

            sender.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(msgId)
                    .text(viewBuilder.title())
                    .replyMarkup(viewBuilder.markup(chatId, fromAi))
                    .build()
            );

            log.info("Переключили режим на {} для chatId={}", s.isTestMode() ? "TESTNET" : "REAL", chatId);
        } finally {
            BotContext.clear();
        }
    }
}
