package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class BotInfoCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    @Override
    public BotCallback callback() {
        return BotCallback.BOT_INFO;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        String text = getBotInfoText();

        var keyboard = keyboardService.getMainMenu(chatId);
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }

    /**
     * Метод для получения информации о боте.
     * Это позволяет легко редактировать текст без изменений в коде.
     */
    private String getBotInfoText() {
        return """
                🤖 <b>О боте</b>

                Этот бот создан для <b>AI-автотpговли</b> на криптобиржах.

                💡 <b>Возможности:</b>
                • Авто-торговля по стратегиям: SMA, RSI, MACD, AI и др.
                • Выбор биржи: Binance, Bybit, KuCoin
                • Поддержка демо и реального режима
                • Графики, индикаторы, мониторинг
                • История сделок и статистика

                👨‍💼 <b>Как начать:</b>
                1. Настроить параметры в меню
                2. Выбрать стратегию и режим
                3. Запустить AI-движок

                💬 По вопросам — пишите в поддержку.
                """;
    }
}
