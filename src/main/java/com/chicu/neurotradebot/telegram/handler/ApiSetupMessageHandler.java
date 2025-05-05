package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ApiSetupMessageHandler implements MessageHandler {

    private final UserService userService;
    private final AiTradeSettingsService cfgService;
    private final ApiCredentialsService credService;
    private final NetworkSettingsViewBuilder netView;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        if (!u.hasMessage() || u.getMessage().getText() == null) return false;
        Long chatId = u.getMessage().getChatId();
        User user = userService.getOrCreate(chatId);
        AiTradeSettings cfg = cfgService.getOrCreate(user);
        return cfg.getApiSetupStep() == ApiSetupStep.ENTER_KEY
                || cfg.getApiSetupStep() == ApiSetupStep.ENTER_SECRET;
    }

    @Override
    public void handle(Update u) throws Exception {
        Message incoming = u.getMessage();
        Long chatId = incoming.getChatId();
        String text  = incoming.getText().trim();

        BotContext.setChatId(chatId);
        try {
            User user = userService.getOrCreate(chatId);
            AiTradeSettings cfg = cfgService.getOrCreate(user);

            // 1) Удаляем старую подсказку, если она есть
            Integer oldPromptId = cfg.getApiSetupPromptMsgId();
            if (oldPromptId != null) {
                sender.executeSilently(
                        DeleteMessage.builder()
                                .chatId(chatId.toString())
                                .messageId(oldPromptId)
                                .build()
                );
            }

            if (cfg.getApiSetupStep() == ApiSetupStep.ENTER_KEY) {
                // 2) ENTER_KEY: сохраняем ключ
                credService.saveApiKey(user, cfg.getExchange(), cfg.isTestMode(), text);
                // переключаем шаг
                cfg.setApiSetupStep(ApiSetupStep.ENTER_SECRET);
                cfgService.save(cfg);

                // 3) Отправляем новую подсказку и сохраняем её ID
                Message prompt = sender.execute(
                        SendMessage.builder()
                                .chatId(chatId.toString())
                                .text("🔐 Отлично! Теперь введите API Secret:")
                                .build()
                );
                cfg.setApiSetupPromptMsgId(prompt.getMessageId());
                cfgService.save(cfg);

            } else {
                // 4) ENTER_SECRET: сохраняем секрет
                credService.saveApiSecret(user, cfg.getExchange(), cfg.isTestMode(), text);
                // сбрасываем шаг и удаляем ID подсказки
                cfg.setApiSetupStep(ApiSetupStep.NONE);
                cfg.setApiSetupPromptMsgId(null);
                cfgService.save(cfg);

                // 5) Показываем меню сетевых настроек
                sender.execute(
                        SendMessage.builder()
                                .chatId(chatId.toString())
                                .text(netView.title())
                                .replyMarkup(netView.markup(chatId, /* fromAi */ false))
                                .build()
                );
            }

        } finally {
            BotContext.clear();
        }
    }
}
