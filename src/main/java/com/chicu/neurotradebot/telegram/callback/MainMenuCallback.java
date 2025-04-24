package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработчик для отображения главного меню.
 * Отправляет пользователю главное меню с кнопками для навигации.
 */
@Component
@RequiredArgsConstructor
public class MainMenuCallback implements CallbackProcessor {

    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    /**
     * Возвращает тип колбека для данного обработчика.
     * В данном случае, это колбек для главного меню.
     *
     * @return тип колбека
     */
    @Override
    public BotCallback callback() {
        return BotCallback.MAIN_MENU;
    }

    /**
     * Обрабатывает колбек для отображения главного меню.
     * Отправляет пользователю клавиатуру с основными действиями.
     *
     * @param chatId      идентификатор чата
     * @param messageId   идентификатор сообщения для редактирования
     * @param callbackData данные колбека
     * @param sender      объект для отправки сообщений
     */
    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        try {
            // Текст главного меню
            String text = "🏠 Главное меню:\nВыберите действие ниже:";

            // Получаем клавиатуру для главного меню
            var keyboard = keyboardService.getMainMenu(chatId);

            // Редактируем сообщение с главным меню
            messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
        } catch (Exception e) {
            // Обработка ошибок, если что-то пошло не так
            String errorMessage = "❌ Произошла ошибка при загрузке главного меню.";
            messageUtils.sendMessage(chatId, errorMessage, sender);
            // Логируем ошибку для отладки
            e.printStackTrace();
        }
    }
}
