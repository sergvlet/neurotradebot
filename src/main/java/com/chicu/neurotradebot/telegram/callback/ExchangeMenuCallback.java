package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработчик для отображения меню выбора биржи.
 * Отправляет пользователю список доступных бирж для торговли.
 */
@Component
@RequiredArgsConstructor
public class ExchangeMenuCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    /**
     * Возвращает тип колбека для данного обработчика.
     * В данном случае, это колбек для выбора биржи.
     *
     * @return тип колбека
     */
    @Override
    public BotCallback callback() {
        return BotCallback.EXCHANGE_MENU;
    }

    /**
     * Обрабатывает колбек для выбора биржи.
     * Отправляет пользователю клавиатуру с доступными биржами.
     *
     * @param chatId      идентификатор чата
     * @param messageId   идентификатор сообщения для редактирования
     * @param callbackData данные колбека
     * @param sender      объект для отправки сообщений
     */
    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        try {
            // Получаем клавиатуру для выбора биржи
            var keyboard = keyboardService.getExchangeSelectionMenu();
            String text = "📈 Выберите биржу для торговли:";

            // Редактируем сообщение с меню выбора биржи
            messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
        } catch (Exception e) {
            // Обработка ошибок, если что-то пошло не так
            String errorMessage = "❌ Произошла ошибка при загрузке меню выбора биржи.";
            messageUtils.sendMessage(chatId, errorMessage, sender);
            // Можно логировать ошибку для отладки
            e.printStackTrace();
        }
    }
}
