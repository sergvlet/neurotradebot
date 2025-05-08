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
        int fast = cfg.getMacdFast();
        int slow = cfg.getMacdSlow();
        int signal = cfg.getMacdSignal();

        // массив closes
        BigDecimal[] closes = history.stream()
                                     .map(Bar::getClose)
                                     .toArray(BigDecimal[]::new);

        // EMA
        BigDecimal[] emaFast = calculateEMA(closes, fast);
        BigDecimal[] emaSlow = calculateEMA(closes, slow);

        // начинаем MACD там, где есть оба EMA
        int start = slow - 1;
        int len   = closes.length - start;
        BigDecimal[] macdLine = new BigDecimal[len];
        for (int i = start; i < closes.length; i++) {
            macdLine[i - start] = emaFast[i].subtract(emaSlow[i]);
        }

        // сигнальная линия на этом массиве
        BigDecimal[] signalLine = calculateEMA(macdLine, signal);

        // берём последние две точки обеих линий
        int idxCurr = macdLine.length - 1;
        int idxPrev = idxCurr - 1;
        BigDecimal prevMacd   = macdLine[idxPrev];
        BigDecimal prevSignal = signalLine[idxPrev];
        BigDecimal currMacd   = macdLine[idxCurr];
        BigDecimal currSignal = signalLine[idxCurr];

        // кроссовер
        if (prevMacd.compareTo(prevSignal) <= 0 && currMacd.compareTo(currSignal) > 0) {
            return Signal.BUY;
        }
        if (prevMacd.compareTo(prevSignal) >= 0 && currMacd.compareTo(currSignal) < 0) {
            return Signal.SELL;
        }
        return Signal.HOLD;
    }

    private BigDecimal[] calculateEMA(BigDecimal[] data, int period) {
        BigDecimal[] ema = new BigDecimal[data.length];
        BigDecimal k = BigDecimal.valueOf(2.0 / (period + 1));
        // первое значение EMA = SMA первых period
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(data[i]);
        }
        ema[period - 1] = sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        // дальше по формуле
        for (int i = period; i < data.length; i++) {
            ema[i] = data[i]
                    .subtract(ema[i - 1])
                    .multiply(k)
                    .add(ema[i - 1]);
        }
        return ema;
    }
}
