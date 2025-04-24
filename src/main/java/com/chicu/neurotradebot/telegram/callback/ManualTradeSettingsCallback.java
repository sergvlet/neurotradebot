package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработчик для отображения настроек ручной торговли.
 * Отправляет пользователю меню с доступными настройками для ручной торговли.
 */
@Component
@RequiredArgsConstructor
public class ManualTradeSettingsCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;

    /**
     * Возвращает тип колбека для данного обработчика.
     * В данном случае, это колбек для настроек ручной торговли.
     *
     * @return тип колбека
     */
    @Override
    public BotCallback callback() {
        return BotCallback.MANUAL_TRADE_SETTINGS;
    }

    /**
     * Обрабатывает колбек для отображения настроек ручной торговли.
     * Отправляет пользователю клавиатуру с доступными настройками для ручной торговли.
     *
     * @param chatId      идентификатор чата
     * @param messageId   идентификатор сообщения для редактирования
     * @param data        данные колбека
     * @param sender      объект для отправки сообщений
     */
    @Override
    public void process(Long chatId, Integer messageId, String data, AbsSender sender) {
        try {
            // Текст для отображения меню настроек ручной торговли
            String text = "⚙️ <b>Настройки ручной торговли</b>";

            // Получаем клавиатуру для настроек ручной торговли
            var keyboard = keyboardService.getManualTradeSettingsMenu(chatId);

            // Редактируем сообщение с меню настроек
            messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
        } catch (Exception e) {
            // Обработка ошибок, если что-то пошло не так
            String errorMessage = "❌ Произошла ошибка при загрузке настроек ручной торговли.";
            messageUtils.sendMessage(chatId, errorMessage, sender);

            // Логируем ошибку для отладки
            e.printStackTrace();
        }
    }
}
