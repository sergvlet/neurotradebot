// src/main/java/com/chicu/neurotradebot/handler/ApiSetupMessageHandler.java
package com.chicu.neurotradebot.handler;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.MessageHandler;
import com.chicu.neurotradebot.view.NetworkSettingsViewBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        if (!u.hasMessage() || u.getMessage().getText() == null) {
            return false;
        }
        Long chatId = u.getMessage().getChatId();
        User user = userService.getOrCreate(chatId);
        AiTradeSettings cfg = cfgService.getOrCreate(user);
        // обрабатываем только шаги ENTER_KEY и ENTER_SECRET
        return cfg.getApiSetupStep() == ApiSetupStep.ENTER_KEY
            || cfg.getApiSetupStep() == ApiSetupStep.ENTER_SECRET;
    }

    @Override
    public void handle(Update u) throws Exception {
        Message msg = u.getMessage();
        Long chatId = msg.getChatId();
        String text  = msg.getText().trim();

        BotContext.setChatId(chatId);
        try {
            User user = userService.getOrCreate(chatId);
            AiTradeSettings cfg = cfgService.getOrCreate(user);

            if (cfg.getApiSetupStep() == ApiSetupStep.ENTER_KEY) {
                // 1) сохраним введённый API Key
                credService.saveApiKey(user, cfg.getExchange(), cfg.isTestMode(), text);
                // 2) переключим шаг на ввод секрет
                cfg.setApiSetupStep(ApiSetupStep.ENTER_SECRET);
                cfgService.save(cfg);
                // 3) попросим ввести секрет, без удаления предыдущего системного сообщения
                sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("🔐 Теперь введите API Secret:")
                    .build());

            } else { // ENTER_SECRET
                // 1) сохраним секрет
                credService.saveApiSecret(user, cfg.getExchange(), cfg.isTestMode(), text);
                // 2) сбросим шаг
                cfg.setApiSetupStep(ApiSetupStep.NONE);
                cfgService.save(cfg);
                // 3) покажем обновлённые сетевые настройки без лишнего спама
                sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(netView.title())
                    .replyMarkup(netView.markup(chatId, /* fromAi */ false))
                    .build());
            }

        } finally {
            BotContext.clear();
        }
    }
}
