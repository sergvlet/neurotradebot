package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.trade.service.TradingStatusService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import com.chicu.neurotradebot.trade.service.ai.HybridAiEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;


@Component
@RequiredArgsConstructor
public class StartTradeCallback implements CallbackProcessor {

    private final HybridAiEngine hybridAiEngine;
    private final TradingStatusService tradingStatusService;
    private final UserSettingsService userSettingsService;
    private final MessageUtils messageUtils;

    @Override
    public BotCallback callback() {
        return BotCallback.START_TRADE;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        UserSettings settings = userSettingsService.getOrCreate(chatId);

        // Проверка, что все необходимые параметры установлены
        if (settings.getExchangeSymbol() == null || settings.getTradeLimit() == null || settings.getStrategies().isEmpty()) {
            String warningText = """
                    ❗️ Для начала торговли нужно установить следующие параметры:
                    1. Биржа
                    2. Символ
                    3. Лимит сделки
                    4. Стратегии""";
            messageUtils.editMessage(chatId, messageId, warningText, null, sender);
            return;
        }

        // Отправляем информацию о настройках торговли
        String text = """
                🔄 <b>Запуск AI-торговли</b>
                ⚙️ <b>Настройки:</b>
                • Биржа: %s
                • Символ: %s
                • Таймфрейм: %s
                • Лимит: %s USDT
                • Стратегии: %s
                • Режим: %s
                                               \s
                ⏳ Ожидается анализ рынка...
               \s""".formatted(
                settings.getExchange(),
                settings.getExchangeSymbol(),
                settings.getTimeframe(),
                settings.getTradeLimit(),
                settings.getStrategyText(),
                settings.getTradeMode()
        );

        var keyboard = tradingStatusService.getActiveTradeStatusKeyboard();
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);

        // Регистрируем сообщение и запускаем торговлю
        tradingStatusService.register(chatId, messageId);
        hybridAiEngine.runForUser(chatId);
    }
}

