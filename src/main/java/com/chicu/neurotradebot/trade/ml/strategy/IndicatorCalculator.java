package com.chicu.neurotradebot.trade.ml.strategy;

import com.chicu.neurotradebot.entity.Bar;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Утилита для расчёта RSI(14), Bollinger Bands(20,2), ATR(14) и bodyRatio.
 */
public class IndicatorCalculator {

    @Data
    @AllArgsConstructor
    public static class IndicatorValues {
        private double rsi;
        private double bbUpper;
        private double bbLower;
        private double bbWidth;
        private double atr;
        private double bodyRatio;
    }

    public static IndicatorValues calculate(List<Bar> bars) {
        if (bars == null || bars.size() < 20) {
            throw new IllegalArgumentException("Недостаточно баров для расчёта индикаторов (нужно хотя бы 20)");
        }

        // RSI 14
        double gainSum = 0, lossSum = 0;
        for (int i = bars.size() - 14; i < bars.size(); i++) {
            BigDecimal change = bars.get(i).getClose().subtract(bars.get(i - 1).getClose());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                gainSum += change.doubleValue();
            } else {
                lossSum += -change.doubleValue();
            }
        }
        double avgGain = gainSum / 14;
        double avgLoss = lossSum / 14;
        double rs = avgLoss == 0 ? 100 : avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + rs));

        // Bollinger Bands 20,2
        List<Double> closes = bars.subList(bars.size() - 20, bars.size())
                                  .stream()
                                  .map(b -> b.getClose().doubleValue())
                                  .toList();
        double sum = closes.stream().mapToDouble(Double::doubleValue).sum();
        double sma = sum / 20;
        double variance = closes.stream()
                .mapToDouble(d -> Math.pow(d - sma, 2))
                .sum() / 20;
        double sd = Math.sqrt(variance);
        double bbUpper = sma + 2 * sd;
        double bbLower = sma - 2 * sd;
        double bbWidth = (bbUpper - bbLower) / sma * 100;

        // ATR 14
        double trSum = 0;
        for (int i = bars.size() - 14; i < bars.size(); i++) {
            Bar curr = bars.get(i);
            Bar prev = bars.get(i - 1);
            double highLow = curr.getHigh().subtract(curr.getLow()).doubleValue();
            double highClose = curr.getHigh().subtract(prev.getClose()).abs().doubleValue();
            double lowClose  = curr.getLow().subtract(prev.getClose()).abs().doubleValue();
            trSum += Math.max(highLow, Math.max(highClose, lowClose));
        }
        double atr = trSum / 14;

        // Body ratio последней свечи
        Bar last = bars.get(bars.size() - 1);
        double body = last.getClose().subtract(last.getOpen()).abs().doubleValue();
        double range = last.getHigh().subtract(last.getLow()).doubleValue();
        double bodyRatio = range == 0 ? 0 : (body / range) * 100;

        // Округлим до двух знаков
        rsi       = new BigDecimal(rsi).setScale(2, RoundingMode.HALF_UP).doubleValue();
        bbUpper   = new BigDecimal(bbUpper).setScale(8, RoundingMode.HALF_UP).doubleValue();
        bbLower   = new BigDecimal(bbLower).setScale(8, RoundingMode.HALF_UP).doubleValue();
        bbWidth   = new BigDecimal(bbWidth).setScale(2, RoundingMode.HALF_UP).doubleValue();
        atr       = new BigDecimal(atr).setScale(8, RoundingMode.HALF_UP).doubleValue();
        bodyRatio = new BigDecimal(bodyRatio).setScale(2, RoundingMode.HALF_UP).doubleValue();

        return new IndicatorValues(rsi, bbUpper, bbLower, bbWidth, atr, bodyRatio);
    }
}
