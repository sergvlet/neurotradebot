package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RsiMacdStrategyImpl implements TradingStrategy {

    @Override
    public Signal generateSignal(String symbol,
                                 List<Bar> history,
                                 AiTradeSettings settings) {
        RsiMacdConfig cfg = settings.getRsiMacdConfig();
        if (cfg == null) {
            return Signal.HOLD;
        }

        int fast = cfg.getMacdFast();
        int slow = cfg.getMacdSlow();
        int signal = cfg.getMacdSignal();
        int rsiPeriod = cfg.getRsiPeriod();

        BigDecimal[] closes = history.stream()
                .map(Bar::getClose)
                .toArray(BigDecimal[]::new);

        BigDecimal[] emaFast = calculateEMA(closes, fast);
        BigDecimal[] emaSlow = calculateEMA(closes, slow);

        BigDecimal[] macdLine = new BigDecimal[emaFast.length];
        for (int i = 0; i < emaFast.length; i++) {
            macdLine[i] = emaFast[i].subtract(emaSlow[i]);
        }

        BigDecimal[] sigLine = calculateEMA(macdLine, signal);

        int idx = macdLine.length - 1;
        BigDecimal prevMacd = macdLine[idx - 1], prevSig = sigLine[idx - 1];
        BigDecimal currMacd = macdLine[idx], currSig = sigLine[idx];

        // MACD Crossover
        if (prevMacd.compareTo(prevSig) <= 0 && currMacd.compareTo(currSig) > 0) {
            // перед фильтром RSI
            BigDecimal rsi = calculateRSI(closes, rsiPeriod);
            if (rsi.compareTo(cfg.getRsiLower()) < 0) {
                return Signal.BUY;
            }
        }
        if (prevMacd.compareTo(prevSig) >= 0 && currMacd.compareTo(currSig) < 0) {
            BigDecimal rsi = calculateRSI(closes, rsiPeriod);
            if (rsi.compareTo(cfg.getRsiUpper()) > 0) {
                return Signal.SELL;
            }
        }

        return Signal.HOLD;
    }

    private BigDecimal[] calculateEMA(BigDecimal[] data, int period) {
        BigDecimal[] ema = new BigDecimal[data.length];
        BigDecimal k = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(data[i]);
        }
        ema[period - 1] = sum.divide(BigDecimal.valueOf(period), BigDecimal.ROUND_HALF_UP);
        for (int i = period; i < data.length; i++) {
            ema[i] = data[i]
                    .subtract(ema[i - 1])
                    .multiply(k)
                    .add(ema[i - 1]);
        }
        return ema;
    }

    private BigDecimal calculateRSI(BigDecimal[] data, int period) {
        BigDecimal gain = BigDecimal.ZERO, loss = BigDecimal.ZERO;
        for (int i = 1; i <= period; i++) {
            BigDecimal change = data[i].subtract(data[i - 1]);
            if (change.signum() > 0) gain = gain.add(change);
            else loss = loss.add(change.abs());
        }
        BigDecimal avgGain = gain.divide(BigDecimal.valueOf(period), BigDecimal.ROUND_HALF_UP);
        BigDecimal avgLoss = loss.divide(BigDecimal.valueOf(period), BigDecimal.ROUND_HALF_UP);
        BigDecimal rs = avgLoss.signum() == 0
                ? BigDecimal.ZERO
                : avgGain.divide(avgLoss, BigDecimal.ROUND_HALF_UP);
        return BigDecimal.ONE
                .add(rs).pow(-1)
                .multiply(BigDecimal.valueOf(100));
    }
}
