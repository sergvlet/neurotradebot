package com.chicu.neurotradebot.telegram.handler.settings;

import com.chicu.neurotradebot.telegram.handler.menu.SettingsMenuBuilder;
import com.chicu.neurotradebot.telegram.session.UserSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class SettingsMenuHandler {

    private final SettingsMenuBuilder settingsMenuBuilder;

    public Object handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String data = update.getCallbackQuery().getData();

        // Если нажата кнопка переключения режима Test/Real
        if ("switch_mode".equals(data)) {
            UserSessionManager.toggleTestnet(chatId);
        }

        // Получаем данные сессии
        boolean isTestnet = UserSessionManager.isTestnet(chatId);
        String network = isTestnet ? "Test (тестовая сеть)" : "Real (реальная сеть)";
        String exchange = UserSessionManager.getSelectedExchange(chatId);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(String.format("""
                        ⚙️ *Настройки торговли:*

                        Сеть: *%s*
                        Биржа: *%s*

                        Выберите действие:
                        """, network, exchange))
                .replyMarkup(settingsMenuBuilder.buildSettingsMenu())
                .parseMode("Markdown")
                .build();
    }
}
