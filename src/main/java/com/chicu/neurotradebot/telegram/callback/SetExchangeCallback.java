package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.service.UserApiKeysService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработчик для колбека выбора биржи.
 * Позволяет пользователю выбрать биржу и либо сразу перейти в меню выбора символа,
 * либо ввести API ключ.
 */
@Component
@RequiredArgsConstructor
public class SetExchangeCallback implements CallbackProcessor {

    private final UserSettingsService settingsService;
    private final UserApiKeysService apiKeysService;
    private final MessageUtils messageUtils;
    private final ExchangeMenuCallback exchangeMenuCallback;

    private static final String API_KEY_TEST = "API_KEY_TEST";
    private static final String API_KEY_REAL = "API_KEY_REAL";

    /**
     * Возвращает тип колбека для данного обработчика.
     *
     * @return тип колбека
     */
    @Override
    public BotCallback callback() {
        return BotCallback.SET_EXCHANGE;
    }

    /**
     * Обрабатывает колбек для выбора биржи и действий с API ключами.
     * Запрашивает API ключи, если их нет, или сразу переходит в меню выбора символа.
     *
     * @param chatId      идентификатор чата
     * @param messageId   идентификатор сообщения для редактирования
     * @param callbackData данные колбека
     * @param sender      объект для отправки сообщений
     */
    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        try {
            // Разделяем данные колбека, чтобы получить выбранную биржу
            String[] parts = callbackData.split(":");
            if (parts.length < 2) {
                messageUtils.editMessage(chatId, messageId, "❌ Ошибка: биржа не указана", null, sender);
                return;
            }

            // Получаем биржу из данных колбека
            Exchange selected = Exchange.valueOf(parts[1]);

            // Получаем настройки пользователя
            var settings = settingsService.getOrCreate(chatId);
            settings.setExchange(selected);
            settingsService.setExchange(chatId, selected);

            // Проверяем, используется ли демонстрационный режим (по умолчанию)
            boolean isDemo = settings.getTradeMode() == null || settings.getTradeMode().name().equals("DEMO");

            // Проверяем, есть ли уже API ключи
            boolean hasKeys = isDemo
                    ? apiKeysService.hasTestKeys(chatId, selected)
                    : apiKeysService.hasRealKeys(chatId, selected);

            if (hasKeys) {
                // ✅ Ключи уже есть — сразу переходим в меню выбора символа
                exchangeMenuCallback.process(chatId, messageId, BotCallback.EXCHANGE_MENU.getValue(), sender);
            } else {
                // 🔐 Ключей нет — просим ввести API Key
                messageUtils.editMessage(chatId, messageId,
                        "🔐 Введите API Key для биржи " + selected + (isDemo ? " (тестовый режим):" : " (реальный режим):"),
                        null, sender);

                // Устанавливаем состояние ожидания ввода для API ключей
                settingsService.setWaitingForInput(chatId,
                        (isDemo ? API_KEY_TEST : API_KEY_REAL) + ":" + selected.name());
            }
        } catch (IllegalArgumentException e) {
            // Если биржа не найдена, отправляем сообщение об ошибке
            messageUtils.editMessage(chatId, messageId, "❌ Ошибка: неверная биржа", null, sender);
        } catch (Exception e) {
            // Логируем и обрабатываем все остальные ошибки
            messageUtils.editMessage(chatId, messageId, "❌ Произошла ошибка, попробуйте снова.", null, sender);
            e.printStackTrace();
        }
    }
}
