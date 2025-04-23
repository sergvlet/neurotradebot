package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.enums.TradeType;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class SetTradeTypeCallback implements CallbackProcessor {

    private final UserSettingsService userSettingsService;
    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;

    @Override
    public BotCallback callback() {
        return BotCallback.SET_TRADE_TYPE;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        try {
            String[] parts = callbackData.split(":");
            TradeType type = TradeType.valueOf(parts[1]); // пример: set_trade_type:AI

            userSettingsService.setTradeType(chatId, type);

            String text = type == TradeType.AI
                    ? "🤖 Режим торговли: <b>AI</b>\n\nВыберите действие:"
                    : "✋ Режим торговли: <b>Ручной</b>\n\nВыберите действие:";

            messageUtils.editMessage(chatId, messageId, text, keyboardService.getTradingMenuByMode(chatId), sender);

        } catch (Exception e) {
            messageUtils.editMessage(chatId, messageId, "❌ Ошибка переключения режима.", null, sender);
        }
    }
}
