package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработчик для колбека выбора режима торговли.
 * Позволяет пользователю выбрать между доступными режимами торговли.
 */
@Component
@RequiredArgsConstructor
public class SelectModeCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;
    private final UserSettingsService userSettingsService;

    /**
     * Возвращает тип колбека для данного обработчика.
     * В данном случае, это колбек для выбора режима торговли.
     *
     * @return тип колбека
     */
    @Override
    public BotCallback callback() {
        return BotCallback.SELECT_MODE;
    }

    /**
     * Обрабатывает колбек для выбора режима торговли.
     * Показывает пользователю меню с выбором режима торговли и текущим выбранным режимом.
     *
     * @param chatId      идентификатор чата
     * @param messageId   идентификатор сообщения для редактирования
     * @param callbackData данные колбека
     * @param sender      объект для отправки сообщений
     */
    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        try {
            // Получаем текущий выбранный режим торговли
            TradeMode selected = userSettingsService.getTradeMode(chatId);

            // Если режим торговли не выбран, устанавливаем по умолчанию режим DEMO
            if (selected == null) {
                selected = TradeMode.DEMO; // режим по умолчанию
                userSettingsService.setTradeMode(chatId, selected); // сохраняем в настройках пользователя
            }

            // Получаем клавиатуру для выбора режима
            InlineKeyboardMarkup keyboard = keyboardService.getModeSelectionMenu(chatId, selected);

            // Подготовка текста для отображения пользователю
            String text = "🧪 Выберите режим торговли:\n\n" + selected.getTitle();

            // Отправляем отредактированное сообщение с клавиатурой
            messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
        } catch (Exception e) {
            // Обработка исключений
            String errorMessage = "❌ Произошла ошибка при выборе режима торговли.";
            messageUtils.sendMessage(chatId, errorMessage, sender);

            // Логируем ошибку для дальнейшего анализа
            e.printStackTrace();
        }
    }
}
