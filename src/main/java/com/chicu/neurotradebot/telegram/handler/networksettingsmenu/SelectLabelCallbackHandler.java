// src/main/java/com/chicu/neurotradebot/telegram/handler/networksettingsmenu/SelectLabelCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.networksettingsmenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.ApiCredentialsService;
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
public class SelectLabelCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final ApiCredentialsService credentialsService;
    private final NetworkSettingsViewBuilder viewBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData().startsWith("select.label:");
    }

    @Override
    public void handle(Update update) throws Exception {
        var cq    = update.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        Integer msgId = cq.getMessage().getMessageId();

        try {
            // Извлекаем метку из callbackData
            String label = cq.getData().substring("select.label:".length());

            // Активируем её
            User user = userService.getOrCreate(chat);
            AiTradeSettings s = settingsService.getOrCreate(user);
            credentialsService.selectCredential(
                    user, s.getExchange(), s.isTestMode(), label);
            log.info("✅ Label '{}' активирован для user={}", label, user.getTelegramUserId());

            // Ответ на spinner
            sender.execute(new AnswerCallbackQuery(cq.getId()));

            // Перерисовываем меню сетевых настроек
            sender.execute(
                    EditMessageText.builder()
                            .chatId(chat.toString())
                            .messageId(msgId)
                            .text(viewBuilder.title())
                            .replyMarkup(viewBuilder.markup(chat))  // <-- только chatId
                            .build()
            );
        } finally {
            BotContext.clear();
        }
    }
}
