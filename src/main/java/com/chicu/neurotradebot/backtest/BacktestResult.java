// src/main/java/com/chicu/neurotradebot/backtest/BacktestResult.java
package com.chicu.neurotradebot.backtest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class BacktestResult {
    public static class Trade {
        public final Instant entryTime;
        public final Instant exitTime;
        public final BigDecimal entryPrice;
        public final BigDecimal exitPrice;
        public final BigDecimal pnl; // прибыль/убыток
        public Trade(Instant e1, Instant e2, BigDecimal p1, BigDecimal p2, BigDecimal pnl) {
            entryTime = e1; exitTime = e2; entryPrice = p1; exitPrice = p2; this.pnl = pnl;
        }
    }

    public final List<Trade> trades;
    public final List<Point> equityCurve; // точки (время, баланс)
    public final BigDecimal totalReturn;
    public final BigDecimal maxDrawdown;
    public final BigDecimal averagePnL;
    public final double winRate;
    public final BigDecimal profitFactor;
    public final double avgDurationMinutes;

    public BacktestResult(List<Trade> trades,
                          List<Point> equityCurve,
                          BigDecimal totalReturn,
                          BigDecimal maxDrawdown,
                          BigDecimal averagePnL,
                          double winRate,
                          BigDecimal profitFactor,
                          double avgDurationMinutes) {
        this.trades = trades;
        this.equityCurve = equityCurve;
        this.totalReturn = totalReturn;
        this.maxDrawdown = maxDrawdown;
        this.averagePnL = averagePnL;
        this.winRate = winRate;
        this.profitFactor = profitFactor;
        this.avgDurationMinutes = avgDurationMinutes;
    }

    public static class Point {
        public final Instant time;
        public final BigDecimal balance;
        public Point(Instant t, BigDecimal b) { time = t; balance = b; }
    }
}
