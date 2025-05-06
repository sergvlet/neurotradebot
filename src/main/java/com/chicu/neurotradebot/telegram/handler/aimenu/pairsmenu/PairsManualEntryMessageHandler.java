package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.MessageHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PairsManualEntryMessageHandler implements MessageHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;
    private final AiTradeMenuBuilder aiBuilder;

    @Override
    public boolean canHandle(Update u) {
        if (!u.hasMessage() || u.getMessage().getText() == null) {
            return false;
        }
        Long chatId = u.getMessage().getChatId();
        AiTradeSettings cfg = settingsService.getOrCreate(userService.getOrCreate(chatId));
        return cfg.getApiSetupStep() == ApiSetupStep.ENTER_PAIR_ADD;
    }

    @Override
    public void handle(Update u) throws Exception {
        Long chatId      = u.getMessage().getChatId();
        Integer userMsgId = u.getMessage().getMessageId();
        AiTradeSettings cfg = settingsService.getOrCreate(userService.getOrCreate(chatId));
        Integer promptId = cfg.getApiSetupPromptMsgId();

        BotContext.setChatId(chatId);
        try {
            // 1) Собираем введённые пары
            List<String> pairs = Arrays.stream(u.getMessage().getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            // 2) Сохраняем их и сбрасываем шаг
            cfg.getPairs().clear();
            cfg.getPairs().addAll(pairs);
            cfg.setApiSetupStep(ApiSetupStep.NONE);
            cfg.setApiSetupPromptMsgId(null);
            settingsService.save(cfg);

            // 3) Удаляем подсказку (меню) и само сообщение пользователя
            if (promptId != null) {
                sender.execute(
                        DeleteMessage.builder()
                                .chatId(chatId.toString())
                                .messageId(promptId)
                                .build()
                );
            }
            sender.execute(
                    DeleteMessage.builder()
                            .chatId(chatId.toString())
                            .messageId(userMsgId)
                            .build()
            );

            // 4) Отправляем единственное финальное меню AI
            sender.execute(
                    SendMessage.builder()
                            .chatId(chatId.toString())
                            .text(aiBuilder.title())
                            .replyMarkup(aiBuilder.markup(chatId))
                            .build()
            );

        } finally {
            BotContext.clear();
        }
    }
}
