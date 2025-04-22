package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.repository.ActiveTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActiveTradeService {

    private final ActiveTradeRepository repository;
    private final ClosedTradeService closedTradeService;
    private final TradeLogService tradeLogService;
    private final TelegramNotificationService notificationService;

    /**
     * Сохранить новую открытую сделку
     */
    public void save(Long chatId, String symbol, String amount, String usdtAmount,
                     String strategy, String mode, double openPrice) {
        ActiveTrade trade = new ActiveTrade();
        trade.setChatId(chatId);
        trade.setSymbol(symbol);
        trade.setStrategy(strategy);
        trade.setMode(mode);
        trade.setAmount(amount);
        trade.setUsdtAmount(usdtAmount);
        trade.setOpenPrice(openPrice);
        trade.setOpenTime(ZonedDateTime.now());

        repository.save(trade);

        tradeLogService.log(chatId, symbol, strategy, Signal.BUY.name(), mode,
                "💼 Сделка открыта по цене " + openPrice + " на сумму " + usdtAmount);
        notificationService.notify(chatId, "✅ Сделка открыта: " + symbol + " (" + strategy + ")");
    }

    /**
     * Закрытие сделки — с сохранением в ClosedTrade
     */
    public void close(Long chatId, String symbol, double closePrice) {
        Optional<ActiveTrade> optional = repository.findByChatIdAndSymbol(chatId, symbol);
        if (optional.isEmpty()) return;

        ActiveTrade activeTrade = optional.get();

        var closed = closedTradeService.saveClosedTrade(activeTrade, closePrice);
        repository.delete(activeTrade);

        tradeLogService.log(chatId, symbol, activeTrade.getStrategy(), Signal.SELL.name(),
                activeTrade.getMode(), "💰 Сделка закрыта. Прибыль: " + closed.getProfit());
        notificationService.notify(chatId, "💰 Сделка по " + symbol + " закрыта. Прибыль: " + closed.getProfit());
    }

    public Optional<ActiveTrade> getBySymbol(Long chatId, String symbol) {
        return repository.findByChatIdAndSymbol(chatId, symbol);
    }

    public boolean exists(Long chatId, String symbol) {
        return repository.findByChatIdAndSymbol(chatId, symbol).isPresent();
    }

    public void remove(ActiveTrade trade) {
        repository.delete(trade);
    }
}
