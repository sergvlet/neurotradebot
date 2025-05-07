// src/main/java/com/chicu/neurotradebot/telegram/handler/PairsManualEntryCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.PairsAddMethodMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PairsManualEntryCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;
    private final PairsAddMethodMenuBuilder methodMenuBuilder;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
                && "pairs_manual_entry".equals(u.getCallbackQuery().getData());
    }

    @Override
    @Transactional
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        Long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();

        // ответ на callback-кнопку
        sender.execute(new AnswerCallbackQuery(cq.getId()));


        try {
            // загружаем настройки
            var user     = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);

            // затираем старые пары и переключаем шаг ввода
            settings.getPairs().clear();
            settings.setApiSetupStep(ApiSetupStep.ENTER_PAIR_ADD);
            // сохраняем промежуточно, чтобы получить корректное состояние
            settingsService.save(settings);

            // редактируем текущее сообщение — показываем подсказку ввода
            sender.execute(
                    EditMessageText.builder()
                            .chatId(chat.toString())
                            .messageId(msgId)
                            .text("✍️ Введите одну пару или несколько через запятую (например, BTCUSDT, ETHUSDT):")
                            .replyMarkup(methodMenuBuilder.markup(chat))
                            .build()
            );

            // сохраняем ID этого сообщения, чтобы потом его удалить
            settings.setApiSetupPromptMsgId(msgId);
            settingsService.save(settings);

        } finally {
            BotContext.clear();
        }
    }
}
