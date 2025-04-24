package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработчик для отображения меню ручной торговли.
 * Отправляет пользователю меню с доступными действиями для ручной торговли.
 */
@Component
@RequiredArgsConstructor
public class ManualMenuCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    /**
     * Возвращает тип колбека для данного обработчика.
     * В данном случае, это колбек для меню ручной торговли.
     *
     * @return тип колбека
     */
    @Override
    public BotCallback callback() {
        return BotCallback.MANUAL_MENU;
    }

    /**
     * Обрабатывает колбек для отображения меню ручной торговли.
     * Отправляет пользователю клавиатуру с действиями для ручной торговли.
     *
     * @param chatId      идентификатор чата
     * @param messageId   идентификатор сообщения для редактирования
     * @param callbackData данные колбека
     * @param sender      объект для отправки сообщений
     */
    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        try {
            // Текст для отображения меню
            String text = "📉 <b>Ручная торговля</b>\n\nВыберите действие:";

            // Получаем клавиатуру для ручной торговли
            var keyboard = keyboardService.getManualTradingMenu(chatId);

            // Редактируем сообщение с меню
            messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
        } catch (Exception e) {
            // Обработка ошибок, если что-то пошло не так
            String errorMessage = "❌ Произошла ошибка при загрузке меню ручной торговли.";
            messageUtils.sendMessage(chatId, errorMessage, sender);

            // Логируем ошибку для отладки
            e.printStackTrace();
        }
    }
}
