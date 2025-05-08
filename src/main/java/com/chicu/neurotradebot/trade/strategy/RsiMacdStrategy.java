package com.chicu.neurotradebot.trade.strategy;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class RsiMacdStrategy implements TradingStrategy {

    @Override
    public Signal generateSignal(String symbol,
                                 List<Bar> history,
                                 AiTradeSettings settings) {

        RsiMacdConfig cfg = settings.getRsiMacdConfig();
        if (cfg == null) {
            return Signal.HOLD;
        }

        int fastPeriod   = cfg.getMacdFast();
        int slowPeriod   = cfg.getMacdSlow();
        int signalPeriod = cfg.getMacdSignal();
        int rsiPeriod    = cfg.getRsiPeriod();
        BigDecimal rsiOverbought = cfg.getRsiUpper();
        BigDecimal rsiOversold   = cfg.getRsiLower();

        // проверяем, что баров достаточно
        int minBars = Math.max(slowPeriod + signalPeriod, rsiPeriod) + 1;
        if (history.size() < minBars) {
            return Signal.HOLD;
        }

        // 1) массив цен закрытия
        BigDecimal[] closes = history.stream()
                                     .map(Bar::getClose)
                                     .toArray(BigDecimal[]::new);

        // 2) EMA fast/slow
        BigDecimal[] emaFast = calculateEMA(closes, fastPeriod);
        BigDecimal[] emaSlow = calculateEMA(closes, slowPeriod);

        // 3) MACD-линия на срезе [slow-1..end]
        int start = slowPeriod - 1;
        int len   = closes.length - start;
        BigDecimal[] macdLine = new BigDecimal[len];
        for (int i = start; i < closes.length; i++) {
            macdLine[i - start] = emaFast[i].subtract(emaSlow[i]);
        }

        // 4) Сигнальная линия на MACD-линии
        BigDecimal[] signalLine = calculateEMA(macdLine, signalPeriod);

        // 5) Берём последние два значения MACD и сигнал
        int idxCurr = macdLine.length - 1;
        int idxPrev = idxCurr - 1;
        BigDecimal prevMacd   = macdLine[idxPrev];
        BigDecimal prevSignal = signalLine[idxPrev];
        BigDecimal currMacd   = macdLine[idxCurr];
        BigDecimal currSignal = signalLine[idxCurr];

        // 6) Считаем RSI на всей истории
        BigDecimal rsi = calculateRSI(closes, rsiPeriod);

        // 7) Сигналы MACD+RSI
        boolean crossUp   = prevMacd.compareTo(prevSignal) <= 0 && currMacd.compareTo(currSignal) > 0;
        boolean crossDown = prevMacd.compareTo(prevSignal) >= 0 && currMacd.compareTo(currSignal) < 0;

        if (crossUp && rsi.compareTo(rsiOversold) < 0) {
            return Signal.BUY;
        }
        if (crossDown && rsi.compareTo(rsiOverbought) > 0) {
            return Signal.SELL;
        }

        return Signal.HOLD;
    }

    private BigDecimal[] calculateEMA(BigDecimal[] data, int period) {
        BigDecimal[] ema = new BigDecimal[data.length];
        BigDecimal k = BigDecimal.valueOf(2.0 / (period + 1));

        // инициализация SMA
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(data[i]);
        }
        ema[period - 1] = sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);

        // рекуррентная формула EMA
        for (int i = period; i < data.length; i++) {
            ema[i] = data[i]
                    .subtract(ema[i - 1])
                    .multiply(k)
                    .add(ema[i - 1])
                    .setScale(8, RoundingMode.HALF_UP);
        }
        return ema;
    }

    private BigDecimal calculateRSI(BigDecimal[] data, int period) {
        BigDecimal gain = BigDecimal.ZERO;
        BigDecimal loss = BigDecimal.ZERO;

        // считаем первые period изменений
        for (int i = 1; i <= period; i++) {
            BigDecimal change = data[i].subtract(data[i - 1]);
            if (change.compareTo(BigDecimal.ZERO) > 0) gain = gain.add(change);
            else                                    loss = loss.add(change.abs());
        }
        BigDecimal avgGain = gain.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        BigDecimal avgLoss = loss.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);

        // затем сглаживаем
        for (int i = period + 1; i < data.length; i++) {
            BigDecimal change = data[i].subtract(data[i - 1]);
            BigDecimal g = change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO;
            BigDecimal l = change.compareTo(BigDecimal.ZERO) < 0 ? change.abs() : BigDecimal.ZERO;
            avgGain = avgGain.multiply(BigDecimal.valueOf(period - 1))
                             .add(g)
                             .divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
            avgLoss = avgLoss.multiply(BigDecimal.valueOf(period - 1))
                             .add(l)
                             .divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        }

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        BigDecimal rs = avgGain.divide(avgLoss, 8, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(
               BigDecimal.valueOf(100).divide(rs.add(BigDecimal.ONE), 8, RoundingMode.HALF_UP)
        );
    }
}
