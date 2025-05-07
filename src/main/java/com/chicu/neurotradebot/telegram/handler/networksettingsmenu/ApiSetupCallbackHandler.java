package com.chicu.neurotradebot.telegram.handler.networksettingsmenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.networksettingsmenu.ApiSetupMenuBuilder;
import com.chicu.neurotradebot.telegram.view.networksettingsmenu.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ApiSetupCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService cfgService;
    private final ApiSetupMenuBuilder view;         // билдeр меню API
    private final NetworkSettingsViewBuilder netView;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        if (!u.hasCallbackQuery()) return false;
        String d = u.getCallbackQuery().getData();
        return d.equals("api_setup_start")
                || d.equals("replace_api_key")
                || d.equals("keep_api_key");
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        int  msgId  = cq.getMessage().getMessageId();
        String data = cq.getData();


        try {
            // 1) ответ на callbackQuery (снимет спиннер)
            sender.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(cq.getId())
                    .build());

            // 2) достать cfg
            User user = userService.getOrCreate(chatId);
            AiTradeSettings cfg = cfgService.getOrCreate(user);

            switch (data) {
                case "api_setup_start":
                case "replace_api_key":
                    // переключаем на ввод нового ключа
                    cfg.setApiSetupStep(ApiSetupStep.ENTER_KEY);
                    cfgService.save(cfg);
                    // перерисуем это же сообщение: текст + клавиатура
                    sender.execute(EditMessageText.builder()
                            .chatId(chatId.toString())
                            .messageId(msgId)
                            .text(view.title())
                            .replyMarkup(view.markup())
                            .build());
                    break;

                case "keep_api_key":
                    // отмена, возвращаемся в меню настроек
                    cfg.setApiSetupStep(ApiSetupStep.NONE);
                    cfgService.save(cfg);
                    sender.execute(EditMessageText.builder()
                            .chatId(chatId.toString())
                            .messageId(msgId)
                            .text(netView.title())
                            .replyMarkup(netView.markup(chatId, /*fromAi=*/false))
                            .build());
                    break;
            }
        } finally {
            BotContext.clear();
        }
    }
}