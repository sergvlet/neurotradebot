// src/main/java/com/chicu/neurotradebot/telegram/handler/HelpCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu;

import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class HelpCallbackHandler implements CallbackHandler {

    private final TelegramSender sender;

    public HelpCallbackHandler(TelegramSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
            && "ai_help".equals(u.getCallbackQuery().getData());
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();

        // Отвечаем на callback, чтобы Telegram не показывал «тайм-аут»
        sender.execute(new AnswerCallbackQuery(cq.getId()));


        try {
            // Составляем текст FAQ
            String faq = "❓ *Помощь / FAQ*\n\n" +
                         "*1. Включение AI-режима*\n\n" +
                         "– нажмите «🤖 Включить или отключить AI-режим»\n\n" +
                         "– бот запустит или остановит торговлю по вашим параметрам.\n\n\n" +
                         "*2. Режим торговли*\n\n" +
                         "– «🔀 Режим торговли»\n\n" +
                         "– Spot • Futures USDT/Coin • Margin\n\n\n" +
                         "*3. Валютные пары*\n\n" +
                         "– «💱 Валютные пары»\n\n" +
                         "– Импорт • Ручной ввод • Автонастройка (топ-5 по объёму)\n\n\n" +
                         "*4. Стратегии AI*\n\n" +
                         "– RSI+MACD, EMA Crossover, Grid, DCA, Scalping, Combined\n\n\n" +
                         "*5. Управление рисками*\n\n" +
                         "– «⚠️ Управление рисками»\n\n" +
                         "– Stop-Loss %, Take-Profit %, Max % per trade\n\n\n" +
                         "*6. Интервал сканирования*\n\n" +
                         "– «⏱ Интервал сканирования»\n\n" +
                         "– 1 мин, 5 мин, 15 мин, 1 ч\n\n\n" +
                         "*7. Уведомления*\n\n" +
                         "– «🔔 Настройка уведомлений»\n\n\n" +
                         "*8. Профили настроек*\n\n" +
                         "– «💾 Профили настроек»\n\n\n" +
                         "*9. Автонастройка AI*\n\n" +
                         "– «✨ Автонастройка AI»\n\n\n" +
                         "*10. Графический анализ*\n\n" +
                         "– «📊 Графический анализ»\n\n\n" +
                         "*11. Backtest*\n\n" +
                         "– «🧪 Backtest»\n\n\n" +
                         "*12. Текущие позиции & история*\n\n" +
                         "– «🗂 Текущие позиции», «📜 История сделок»\n";

            // Редактируем текущее сообщение, показываем FAQ
            sender.execute(EditMessageText.builder()
                .chatId(Long.toString(chat))
                .messageId(msgId)
                .text(faq)
                .parseMode("Markdown")
                // Внизу добавим кнопку «⬅️ Назад» для возврата в основное меню
                .replyMarkup(
                  InlineKeyboardMarkup.builder()
                    .keyboard(List.of(
                      List.of(InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("ai_control")
                        .build()
                      )
                    ))
                    .build()
                )
                .build()
            );
        } finally {
            BotContext.clear();
        }
    }
}
