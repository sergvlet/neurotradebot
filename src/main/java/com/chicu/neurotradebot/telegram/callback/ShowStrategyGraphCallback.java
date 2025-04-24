package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.service.ChartService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
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
    private final UserSettingsService userSettingsService;

    @Override
    public BotCallback callback() {
        return BotCallback.SHOW_STRATEGY_GRAPH;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        File chart = chartService.generateStrategyChart(chatId);

        // Получаем текущее состояние пользователя
        var settings = userSettingsService.getOrCreate(chatId);

        if (chart != null && chart.exists() && chart.length() > 0) {
            // Если файл существует, отправляем график
            SendPhoto photo = SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(chart))
                    .caption("📊 График стратегии по выбранным индикаторам")
                    .build();

            try {
                // Отправляем график
                sender.execute(photo);

                // Обновляем текст сообщения
                String text = "📊 График стратегии успешно обновлен!";
                messageUtils.editMessage(chatId, messageId, text, null, sender);

                // Удаляем временный файл после отправки
                if (chart.delete()) {
                    System.out.println("Временный файл удален: " + chart.getAbsolutePath());
                } else {
                    System.err.println("Не удалось удалить временный файл: " + chart.getAbsolutePath());
                }
            } catch (Exception e) {
                messageUtils.sendMessage(chatId, "❌ Ошибка при отправке графика: " + e.getMessage(), sender);
            }
        } else {
            // В случае ошибки при генерации графика
            messageUtils.sendMessage(chatId, "⚠️ Недостаточно данных для построения графика", sender);
        }
    }
}
