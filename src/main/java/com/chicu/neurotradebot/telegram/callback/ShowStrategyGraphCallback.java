package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.File;

@Component
@RequiredArgsConstructor
public class ShowStrategyGraphCallback implements CallbackProcessor {

    private final ChartService chartService;
    private final MessageUtils messageUtils;

    @Override
    public BotCallback callback() {
        return BotCallback.SHOW_STRATEGY_GRAPH;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        File chart = chartService.generateStrategyChart(chatId);

        if (chart != null) {
            SendPhoto photo = SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(chart))
                    .caption("📊 График стратегии по выбранным индикаторам")
                    .build();

            try {
                sender.execute(photo);
            } catch (Exception e) {
                messageUtils.sendMessage(chatId, "❌ Ошибка при отправке графика", sender);
            }
        } else {
            messageUtils.sendMessage(chatId, "⚠️ Недостаточно данных для построения графика", sender);
        }
    }
}
