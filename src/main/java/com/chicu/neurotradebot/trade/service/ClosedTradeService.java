package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import com.chicu.neurotradebot.trade.model.ClosedTrade;
import com.chicu.neurotradebot.trade.repository.ClosedTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClosedTradeService {

    private final ClosedTradeRepository repository;

    public ClosedTrade saveClosedTrade(ActiveTrade activeTrade, double closePrice) {
        ClosedTrade closed = new ClosedTrade();
        closed.setChatId(activeTrade.getChatId());
        closed.setSymbol(activeTrade.getSymbol());
        closed.setStrategy(activeTrade.getStrategy());
        closed.setMode(activeTrade.getMode());

        closed.setAmount(activeTrade.getAmount());
        closed.setUsdtAmount(activeTrade.getUsdtAmount());

        closed.setOpenPrice(activeTrade.getOpenPrice());
        closed.setClosePrice(closePrice);

        try {
            double qty = Double.parseDouble(activeTrade.getAmount());
            double profit = (closePrice - activeTrade.getOpenPrice()) * qty;
            closed.setProfit(profit);
        } catch (NumberFormatException e) {
            closed.setProfit(0.0);
        }

        closed.setOpenTime(activeTrade.getOpenTime());
        closed.setCloseTime(ZonedDateTime.now());

        return repository.save(closed);
    }
    public List<ClosedTrade> findByChatId(Long chatId) {
        return repository.findAllByChatId(chatId);
    }
}
