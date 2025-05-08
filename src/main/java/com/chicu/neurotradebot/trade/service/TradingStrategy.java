// src/main/java/com/chicu/neurotradebot/trade/service/TradingStrategy.java
package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.trade.model.Signal;

import java.util.List;

public interface TradingStrategy {
    /**
     * Сгенерировать сигнал для заданной пары по историческим барам и настройкам.
     */
    Signal generateSignal(String symbol, List<Bar> history, AiTradeSettings settings);

    /**
     * Сколько минимум баров нужно, чтобы стратегия отработала корректно.
     */
    int requiredBars(AiTradeSettings settings);
}
