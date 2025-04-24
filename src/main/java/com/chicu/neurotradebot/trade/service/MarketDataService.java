package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.model.MarketCandle;

import java.util.List;

public interface MarketDataService {
    List<MarketCandle> getCandles(Long chatId, Exchange exchange, TradeMode mode, String symbol, String interval);
}
