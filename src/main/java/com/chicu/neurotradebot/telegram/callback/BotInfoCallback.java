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
    public void process(Long chatId, Integer messageId, AbsSender sender) {
        String text = """
                🤖 <b>О боте</b>

                Этот бот создан для автоматической AI-торговли на криптовалютных биржах.

                💡 Возможности:
                • Авто-торговля по стратегиям: SMA, RSI, MACD, AI и др.
                • Выбор биржи: Binance, Bybit, KuCoin
                • Поддержка демо и реального режима
                • Визуализация графиков и индикаторов
                • Статистика, история сделок и мониторинг

                👨‍💼 Перед началом торговли необходимо:
                1. Зарегистрироваться
                2. Выбрать режим и стратегию
                3. Запустить AI-движок

                💬 По всем вопросам — пишите в поддержку.
                """;

        var keyboard = keyboardService.getMainMenu(chatId);
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
