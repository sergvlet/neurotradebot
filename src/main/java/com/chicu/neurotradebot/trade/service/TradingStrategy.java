package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.Bar;
import com.chicu.neurotradebot.trade.model.Signal;
import java.util.List;

public interface TradingStrategy {

    /**
     * Генерирует торговый сигнал для заданного символа.
     *
     * @param symbol     тикер (например, "BTCUSDT")
     * @param history    список исторических баров (OHLCV)
     * @param settings   текущие настройки AI (risk, strategy params и т.п.)
     * @return           сигнал BUY / SELL / HOLD
     */
    Signal generateSignal(String symbol, List<Bar> history, AiTradeSettings settings);
}
