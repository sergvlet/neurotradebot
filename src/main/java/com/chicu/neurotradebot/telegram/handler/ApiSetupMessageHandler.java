// src/main/java/com/chicu/neurotradebot/telegram/handler/ApiSetupMessageHandler.java
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
import com.chicu.neurotradebot.telegram.view.AiTradeMenuBuilder;
import jakarta.transaction.Transactional;
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
    private final AiTradeMenuBuilder aiView;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        if (!u.hasMessage() || u.getMessage().getText() == null) return false;
        Long chatId = u.getMessage().getChatId();
        User user = userService.getOrCreate(chatId);
        AiTradeSettings cfg = cfgService.getOrCreate(user);
        // обрабатываем три шага: ENTER_KEY, ENTER_SECRET, ENTER_PAIR_ADD
        return switch (cfg.getApiSetupStep()) {
            case ENTER_KEY, ENTER_SECRET, ENTER_PAIR_ADD -> true;
            default -> false;
        };
    }

    @Override
    @Transactional
    public void handle(Update u) throws Exception {
        Message incoming = u.getMessage();
        Long chatId = incoming.getChatId();
        String text  = incoming.getText().trim();

        BotContext.setChatId(chatId);
        try {
            User user = userService.getOrCreate(chatId);
            AiTradeSettings cfg = cfgService.getOrCreate(user);

            // 1) Удаляем старую подсказку, если есть
            Integer oldPromptId = cfg.getApiSetupPromptMsgId();
            if (oldPromptId != null) {
                sender.executeSilently(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(oldPromptId)
                    .build());
            }

            // 2) Ветка ввода API Key
            if (cfg.getApiSetupStep() == ApiSetupStep.ENTER_KEY) {
                credService.saveApiKey(user, cfg.getExchange(), cfg.isTestMode(), text);
                cfg.setApiSetupStep(ApiSetupStep.ENTER_SECRET);
                cfgService.save(cfg);

                Message prompt = sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("🔐 Отлично! Теперь введите API Secret:")
                    .build());
                cfg.setApiSetupPromptMsgId(prompt.getMessageId());
                cfgService.save(cfg);

            // 3) Ветка ввода API Secret
            } else if (cfg.getApiSetupStep() == ApiSetupStep.ENTER_SECRET) {
                credService.saveApiSecret(user, cfg.getExchange(), cfg.isTestMode(), text);
                cfg.setApiSetupStep(ApiSetupStep.NONE);
                cfg.setApiSetupPromptMsgId(null);
                cfgService.save(cfg);

                sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(netView.title())
                    .replyMarkup(netView.markup(chatId, false))
                    .build());

            // 4) Ветка ручного ввода пар
            } else if (cfg.getApiSetupStep() == ApiSetupStep.ENTER_PAIR_ADD) {
                // разбираем ввод: пары через запятую
                String[] tokens = text.split(",");
                for (String t : tokens) {
                    String sym = t.trim().toUpperCase();
                    if (!sym.isEmpty() && !cfg.getPairs().contains(sym)) {
                        cfg.getPairs().add(sym);
                    }
                }
                cfg.setApiSetupStep(ApiSetupStep.NONE);
                cfgService.save(cfg);

                sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("✅ Пары добавлены: " + String.join(", ", cfg.getPairs()))
                    .build());
                sender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(aiView.title())
                    .replyMarkup(aiView.markup(chatId))
                    .build());
            }

        } finally {
            BotContext.clear();
        }
    }
}
