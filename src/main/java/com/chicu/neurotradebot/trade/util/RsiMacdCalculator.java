// src/main/java/com/chicu/neurotradebot/trade/strategy/util/RsiMacdCalculator.java
package com.chicu.neurotradebot.trade.util;



import com.chicu.neurotradebot.enums.Bar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Статические методы для расчёта RSI и MACD.
 */
public class RsiMacdCalculator {

    private static final int SCALE = 8;

    public static BigDecimal calculateRsi(List<Bar> history, int period) {
        int size = history.size();
        if (size < period + 1) {
            throw new IllegalArgumentException("Недостаточно баров для RSI");
        }
        BigDecimal gains = BigDecimal.ZERO, losses = BigDecimal.ZERO;
        for (int i = size - period; i < size; i++) {
            BigDecimal change = history.get(i).getClose()
                .subtract(history.get(i - 1).getClose());
            if (change.compareTo(BigDecimal.ZERO) > 0) gains = gains.add(change);
            else losses = losses.add(change.abs());
        }
        BigDecimal avgGain = gains.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        BigDecimal avgLoss = losses.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        BigDecimal rs = avgGain.divide(avgLoss, SCALE, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100)
            .subtract(BigDecimal.valueOf(100)
            .divide(rs.add(BigDecimal.ONE), SCALE, RoundingMode.HALF_UP));
    }

    public static MacdResult calculateMacd(List<Bar> history, int fast, int slow, int signal) {
        int needed = slow + signal;
        if (history.size() < needed) {
            throw new IllegalArgumentException("Недостаточно баров для MACD");
        }
        List<BigDecimal> closes = new ArrayList<>();
        for (Bar b : history) closes.add(b.getClose());

        List<BigDecimal> fastEma = calculateEmaSeries(closes, fast);
        List<BigDecimal> slowEma = calculateEmaSeries(closes, slow);

        int offset = slow - fast;
        List<BigDecimal> macdSeries = new ArrayList<>();
        for (int i = 0; i < slowEma.size(); i++) {
            macdSeries.add(fastEma.get(i + offset).subtract(slowEma.get(i)));
        }
        List<BigDecimal> signalSeries = calculateEmaSeries(macdSeries, signal);

        BigDecimal macdLine = macdSeries.get(macdSeries.size() - 1);
        BigDecimal signalLine = signalSeries.get(signalSeries.size() - 1);
        return new MacdResult(macdLine, signalLine);
    }

    private static List<BigDecimal> calculateEmaSeries(List<BigDecimal> values, int period) {
        List<BigDecimal> result = new ArrayList<>();
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));

        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) sum = sum.add(values.get(i));
        BigDecimal ema = sum.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        result.add(ema);

        for (int i = period; i < values.size(); i++) {
            ema = values.get(i)
                .subtract(ema)
                .multiply(multiplier)
                .add(ema)
                .setScale(SCALE, RoundingMode.HALF_UP);
            result.add(ema);
        }
        return result;
    }
}
