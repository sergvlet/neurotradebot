package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.ClosedTrade;
import com.chicu.neurotradebot.trade.repository.ClosedTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ClosedTradeRepository closedTradeRepository;

    public String getStatisticsText(Long chatId) {
        List<ClosedTrade> trades = closedTradeRepository.findByChatId(chatId);

        if (trades.isEmpty()) {
            return "📈 У вас пока нет завершённых сделок.";
        }

        int total = trades.size();
        long profitable = trades.stream().filter(t -> t.getProfit() > 0).count();
        double totalProfit = trades.stream().mapToDouble(ClosedTrade::getProfit).sum();

        ClosedTrade lastTrade = trades.stream()
                .max(Comparator.comparing(ClosedTrade::getCloseTime))
                .orElse(null);

        StringBuilder sb = new StringBuilder();
        sb.append("📊 *Ваша статистика:*\n\n")
                .append("• Всего сделок: ").append(total).append("\n")
                .append("• Успешных: ").append(profitable).append("\n")
                .append("• Прибыль: ").append(String.format("%.2f", totalProfit)).append(" USDT\n");

        sb.append("\n📌 *Последняя сделка:*\n")
                .append("• Пара: ").append(lastTrade.getSymbol()).append("\n")
                .append("• Стратегия: ").append(lastTrade.getStrategy()).append("\n")
                .append("• Прибыль: ").append(String.format("%.2f", lastTrade.getProfit())).append(" USDT");

        return sb.toString();
    }
}
