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
            // Проверка на корректность данных callbackData
            if (callbackData == null || !callbackData.contains(":")) {
                messageUtils.editMessage(chatId, messageId, "❌ Неверные данные для переключения режима.", null, sender);
                return;
            }

            // Разделяем данные на части и получаем тип торговли
            String[] parts = callbackData.split(":");
            if (parts.length < 2) {
                messageUtils.editMessage(chatId, messageId, "❌ Неверные данные для переключения режима.", null, sender);
                return;
            }

            TradeType type = TradeType.valueOf(parts[1]); // Пример: set_trade_type:AI

            // Сохраняем выбранный режим в настройках
            userSettingsService.setTradeType(chatId, type);

            // Генерируем текст в зависимости от выбранного режима
            String text = type == TradeType.AI
                    ? "🤖 Режим торговли: <b>AI</b>\n\nВыберите действие:"
                    : "✋ Режим торговли: <b>Ручной</b>\n\nВыберите действие:";

            // Отправляем обновленное сообщение с новым меню
            messageUtils.editMessage(chatId, messageId, text, keyboardService.getTradingMenuByMode(chatId), sender);

        } catch (IllegalArgumentException e) {
            messageUtils.editMessage(chatId, messageId, "❌ Ошибка: Неверно указан режим торговли.", null, sender);
        } catch (Exception e) {
            messageUtils.editMessage(chatId, messageId, "❌ Ошибка переключения режима.", null, sender);
        }
    }
}
