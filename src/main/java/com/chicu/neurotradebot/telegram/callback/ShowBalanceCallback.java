package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.binance.BinanceAccountService;
import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class ShowBalanceCallback implements CallbackProcessor {

    private final BinanceAccountService binanceAccountService;
    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    @Override
    public BotCallback callback() {
        return BotCallback.SHOW_BALANCE;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        try {
            // Получаем отформатированный баланс пользователя
            String balanceText = binanceAccountService.getFormattedBalance(chatId);

            if (balanceText == null || balanceText.isEmpty()) {
                // Если баланс не был получен или пустой, показываем сообщение об ошибке
                balanceText = "❌ Не удалось получить баланс. Попробуйте снова позже.";
            }

            // Получаем клавиатуру для ручной торговли
            var keyboard = keyboardService.getManualTradeSettingsMenu(chatId);

            // Отправляем сообщение с балансом и клавишами
            messageUtils.editMessage(chatId, messageId, balanceText, keyboard, sender);

        } catch (Exception e) {
            // Логируем ошибку и отправляем сообщение об ошибке
            messageUtils.editMessage(chatId, messageId,
                    "❌ Произошла ошибка при получении баланса. Попробуйте снова позже.",
                    keyboardService.getManualTradeSettingsMenu(chatId), sender);
            // Можно логировать исключение для отладки
            e.printStackTrace();
        }
    }
}
