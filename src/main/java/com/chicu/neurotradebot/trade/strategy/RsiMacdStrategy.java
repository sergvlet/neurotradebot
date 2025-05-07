package com.chicu.neurotradebot.trade.strategy;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@Slf4j
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

        BigDecimal[] closes = history.stream()
                .map(Bar::getClose)
                .toArray(BigDecimal[]::new);

        // 1) Вычисляем EMA
        BigDecimal[] emaFast = calculateEMA(closes, fastPeriod);
        BigDecimal[] emaSlow = calculateEMA(closes, slowPeriod);

        // 2) Определяем, с какого индекса у нас уже есть все линии:
        int macdStart    = Math.max(fastPeriod - 1, slowPeriod - 1);
        int signalStart  = macdStart + (signalPeriod - 1);
        if (closes.length <= signalStart) {
            log.warn("Недостаточно баров для MACD: нужно хотя бы {}, а получили {}", signalStart + 1, closes.length);
            return Signal.HOLD;
        }

        // 3) Собираем MACD-линию и сигнальную линию:
        BigDecimal[] macdLine    = new BigDecimal[closes.length];
        for (int i = macdStart; i < closes.length; i++) {
            macdLine[i] = emaFast[i].subtract(emaSlow[i]);
        }
        BigDecimal[] signalLine = calculateEMA(macdLine, signalPeriod);

        // 4) Берём два последних индекса, начиная от signalStart:
        int last = closes.length - 1;
        BigDecimal prevMacd   = macdLine[last - 1];
        BigDecimal prevSignal = signalLine[last - 1];
        BigDecimal currMacd   = macdLine[last];
        BigDecimal currSignal = signalLine[last];

        // 5) Кроссовер:
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
        BigDecimal k = BigDecimal.valueOf(2.0).divide(BigDecimal.valueOf(period + 1), 16, RoundingMode.HALF_UP);
        // инициализация на period-1
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(data[i]);
        }
        ema[period - 1] = sum.divide(BigDecimal.valueOf(period), 16, RoundingMode.HALF_UP);
        // рекуррентная формула
        for (int i = period; i < data.length; i++) {
            ema[i] = data[i]
                    .subtract(ema[i - 1])
                    .multiply(k)
                    .add(ema[i - 1]);
        }
        return ema;
    }
}