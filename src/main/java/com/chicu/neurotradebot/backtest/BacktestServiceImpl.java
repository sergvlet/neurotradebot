// src/main/java/com/chicu/neurotradebot/backtest/BacktestServiceImpl.java
package com.chicu.neurotradebot.backtest;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BacktestServiceImpl implements BacktestService {
    @Override
    public BacktestResult runBacktest(AiTradeSettings cfg) {
        // 1) скачать исторические свечи (через биржевой клиент)
        // 2) прогонять стратегию из TradingServiceImpl, но эмулируя executeOrder -> записывать сделки
        // 3) на каждой сделке обновлять equityCurve: balance = prevBalance + pnl
        // 4) по итогам trades собрать метрики:
        //    totalReturn, maxDrawdown, averagePnL, winRate, profitFactor, avgDuration
        //
        // Для краткости здесь возвращаем пустой результат:
        return new BacktestResult(
            List.of(), List.of(),
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            0.0, BigDecimal.ZERO, 0.0
        );
    }
}
