package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.view.NetworkSettingsMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveExchangeCallbackHandler implements CallbackHandler {

    private static final Map<String, String> EXCHANGES = Map.of(
            "exchange_binance", "Binance",
            "exchange_coinbase", "Coinbase"
    );

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final NetworkSettingsMenuBuilder menuBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(CallbackQuery callbackQuery) {
        return EXCHANGES.containsKey(callbackQuery.getData());
    }

    @Override
    public void handle(CallbackQuery callbackQuery) throws Exception {
        Long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        String callback = callbackQuery.getData();

        BotContext.setChatId(chatId);

        try {
            String selectedExchange = EXCHANGES.get(callback);

            User user = userService.getOrCreate(chatId);
            var settings = settingsService.getOrCreate(user);
            settings.setExchange(selectedExchange);
            settingsService.save(settings);

            sender.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text("Настройки сети:")
                    .replyMarkup(menuBuilder.buildNetworkSettingsMenu(settings.isTestMode(), selectedExchange))
                    .build()
            );

            log.info("Биржа '{}' сохранена для chatId={}", selectedExchange, chatId);
        } finally {
            BotContext.clear();
        }
    }
}
