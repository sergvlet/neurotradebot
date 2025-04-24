package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.telegram.util.NavigationHistoryService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class SettingsCallback implements CallbackProcessor {

    private final KeyboardService keyboardService;
    private final MessageUtils messageUtils;
    private final NavigationHistoryService historyService;
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.SETTINGS;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // сохраняем в историю переход
        historyService.push(chatId, callback());

        // Получаем настройки пользователя, чтобы проверить есть ли что-то для изменения
        var settings = userSettingsService.getOrCreate(chatId);

        // Проверка на наличие настроек у пользователя
        if (settings.getExchange() == null || settings.getStrategies() == null || settings.getStrategies().isEmpty()) {
            messageUtils.editMessage(chatId, messageId, "⚠️ Ваши настройки еще не завершены! Пожалуйста, настройте биржу и стратегии.", null, sender);
            return;
        }

        String text = "⚙️ Настройки:\nВыберите параметр для изменения:";
        InlineKeyboardMarkup keyboard = keyboardService.getManualTradeSettingsMenu(chatId);

        // Отправляем сообщение с кнопками настроек
        messageUtils.editMessage(chatId, messageId, text, keyboard, sender);
    }
}
