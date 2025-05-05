// src/main/java/com/chicu/neurotradebot/trade/strategy/impl/RsiMacdStrategy.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiMacdConfig;

import com.chicu.neurotradebot.enums.Bar;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import com.chicu.neurotradebot.trade.util.MacdResult;
import com.chicu.neurotradebot.trade.util.RsiMacdCalculator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RsiMacdStrategy implements TradingStrategy {

    @Override
    public Signal generateSignal(String symbol,
                                 List<Bar> history,
                                 AiTradeSettings settings) {
        // 1. Достаём конфиг RSI+MACD напрямую
        RsiMacdConfig cfg = settings.getRsiMacdConfig();

        // 2. Проверяем, что баров достаточно для MACD (slow + signal)
        int requiredBars = cfg.getMacdSlow() + cfg.getMacdSignal();
        if (history.size() < requiredBars + 1) {
            // недостаточно данных — воздерживаемся от действий
            return Signal.HOLD;
        }

        // 3. Расчёт RSI и MACD
        BigDecimal rsi = RsiMacdCalculator.calculateRsi(history, cfg.getRsiPeriod());
        MacdResult macd = RsiMacdCalculator.calculateMacd(
            history,
            cfg.getMacdFast(),
            cfg.getMacdSlow(),
            cfg.getMacdSignal()
        );

        // 4. Сигналы на покупку/продажу по порогам из конфига
        boolean buy  = rsi.compareTo(cfg.getRsiLower()) < 0
                    && macd.getMacdLine().compareTo(macd.getSignalLine()) > 0;
        boolean sell = rsi.compareTo(cfg.getRsiUpper()) > 0
                    && macd.getMacdLine().compareTo(macd.getSignalLine()) < 0;

        if (buy)   return Signal.BUY;
        if (sell)  return Signal.SELL;
        return Signal.HOLD;
    }
}
