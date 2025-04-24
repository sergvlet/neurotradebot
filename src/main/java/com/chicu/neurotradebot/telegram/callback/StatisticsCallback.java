package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class StatisticsCallback implements CallbackProcessor {

    private final StatisticsService statisticsService;
    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    @Override
    public BotCallback callback() {
        return BotCallback.STATISTICS;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        // Получаем актуальную статистику для пользователя
        String statisticsText = statisticsService.getStatisticsText(chatId);

        // Отправляем обновленное сообщение со статистикой и клавишей для перехода обратно
        messageUtils.editMessage(
                chatId,
                messageId,
                statisticsText, // обновленный текст со статистикой
                keyboardService.getTradingMenu(chatId), // соответствующая клавиатура
                sender
        );
    }
}
