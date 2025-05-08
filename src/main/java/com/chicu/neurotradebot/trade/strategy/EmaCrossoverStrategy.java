package com.chicu.neurotradebot.trade.strategy;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class EmaCrossoverStrategy implements TradingStrategy {

    private static final int FAST = 12;
    private static final int SLOW = 26;

    @Override
    public Signal generateSignal(String symbol, List<Bar> history, AiTradeSettings settings) {
        BigDecimal[] closes = history.stream()
                .map(Bar::getClose)
                .toArray(BigDecimal[]::new);

        BigDecimal[] emaFast = calculateEMA(closes, FAST);
        BigDecimal[] emaSlow = calculateEMA(closes, SLOW);

        int idx = closes.length - 1;
        BigDecimal prevFast = emaFast[idx - 1], prevSlow = emaSlow[idx - 1];
        BigDecimal currFast = emaFast[idx],     currSlow = emaSlow[idx];

        if (prevFast.compareTo(prevSlow) <= 0 && currFast.compareTo(currSlow) > 0) {
            return Signal.BUY;
        }
        if (prevFast.compareTo(prevSlow) >= 0 && currFast.compareTo(currSlow) < 0) {
            return Signal.SELL;
        }
        return Signal.HOLD;
    }

    @Override
    public int requiredBars(AiTradeSettings settings) {
        // нужно минимум SLOW + 1 бар, чтобы посчитать EMA по медленному периоду
        return SLOW + 1;
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
        // для первых точек, где EMA ещё не рассчитана, заполняем значением первой доступной EMA
        for (int i = 0; i < period - 1; i++) {
            ema[i] = ema[period - 1];
        }
        return ema;
    }
}
