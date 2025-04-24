package com.chicu.neurotradebot.telegram.callback;

import com.chicu.neurotradebot.telegram.util.KeyboardService;
import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.model.ClosedTrade;
import com.chicu.neurotradebot.trade.service.ClosedTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShowClosedTradesCallback implements CallbackProcessor {

    private final ClosedTradeService closedTradeService;
    private final MessageUtils messageUtils;
    private final KeyboardService keyboardService;

    @Override
    public BotCallback callback() {
        return BotCallback.HISTORY;
    }

    @Override
    public void process(Long chatId, Integer messageId, String callbackData, AbsSender sender) {
        List<ClosedTrade> trades = closedTradeService.findByChatId(chatId);

        if (trades.isEmpty()) {
            messageUtils.editMessage(chatId, messageId, "📉 У вас пока нет закрытых сделок.",
                    keyboardService.getTradingMenu(chatId), sender);
            return;
        }

        // Сортируем сделки по времени закрытия (по убыванию)
        List<ClosedTrade> lastFive = trades.stream()
                .sorted(Comparator.comparing(ClosedTrade::getCloseTime).reversed())
                .limit(5)
                .toList();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        StringBuilder sb = new StringBuilder("📉 *5 последних сделок:*\n\n");

        // Формируем строки с информацией о сделках
        for (ClosedTrade trade : lastFive) {
            sb.append("• *").append(trade.getSymbol()).append("* ")
                    .append(trade.getStrategy()).append(" — ")
                    .append(String.format("%.2f", trade.getProfit())).append(" USDT\n")
                    .append("_").append(trade.getOpenTime().format(fmt)).append(" ➝ ")
                    .append(trade.getCloseTime().format(fmt)).append("_\n\n");
        }

        // Ограничиваем длину сообщения для Telegram
        String result = sb.length() > 4000 ? sb.substring(0, 3990) + "..." : sb.toString();

        // Отправляем результат
        messageUtils.editMessage(chatId, messageId, result, keyboardService.getTradingMenu(chatId), sender);
    }
}
