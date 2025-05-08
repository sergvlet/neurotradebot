package com.chicu.neurotradebot.trade.strategy;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MacdStrategy implements TradingStrategy {

    @Override
    public Signal generateSignal(String symbol,
                                 List<Bar> history,
                                 AiTradeSettings settings) {
        // Из настроек берем параметры MACD
        RsiMacdConfig cfg = settings.getRsiMacdConfig();
        int fastPeriod   = cfg.getMacdFast();
        int slowPeriod   = cfg.getMacdSlow();
        int signalPeriod = cfg.getMacdSignal();

        // массив закрытий
        BigDecimal[] closes = history.stream()
                                     .map(Bar::getClose)
                                     .toArray(BigDecimal[]::new);

        // считаем EMA для fast и slow
        BigDecimal[] emaFast  = calculateEMA(closes, fastPeriod);
        BigDecimal[] emaSlow  = calculateEMA(closes, slowPeriod);

        // строим MACD-линию
        BigDecimal[] macdLine = new BigDecimal[closes.length];
        for (int i = 0; i < closes.length; i++) {
            macdLine[i] = emaFast[i].subtract(emaSlow[i]);
        }

        // считаем сигнальную линию (EMA от macdLine)
        BigDecimal[] signalLine = calculateEMA(macdLine, signalPeriod);

        // смотрим на последние два значения, чтобы найти пересечение
        int idx = macdLine.length - 1;
        BigDecimal prevMacd   = macdLine[idx - 1];
        BigDecimal prevSignal = signalLine[idx - 1];
        BigDecimal currMacd   = macdLine[idx];
        BigDecimal currSignal = signalLine[idx];

        if (prevMacd.compareTo(prevSignal) <= 0 && currMacd.compareTo(currSignal) > 0) {
            return Signal.BUY;
        }
        if (prevMacd.compareTo(prevSignal) >= 0 && currMacd.compareTo(currSignal) < 0) {
            return Signal.SELL;
        }
        return Signal.HOLD;
    }

    @Override
    public int requiredBars(AiTradeSettings settings) {
        // Чтобы посчитать MACD и сигнальную линию, нужно минимум:
        // slowPeriod + signalPeriod + 1 бар
        RsiMacdConfig cfg = settings.getRsiMacdConfig();
        return cfg.getMacdSlow() + cfg.getMacdSignal() + 1;
    }

    private BigDecimal[] calculateEMA(BigDecimal[] data, int period) {
        BigDecimal[] ema = new BigDecimal[data.length];
        BigDecimal k = BigDecimal.valueOf(2.0 / (period + 1));
        // начальное значение — простое среднее первых period
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(data[i]);
        }
        ema[period - 1] = sum.divide(BigDecimal.valueOf(period), BigDecimal.ROUND_HALF_UP);
        // рекуррентная формула
        for (int i = period; i < data.length; i++) {
            ema[i] = data[i]
                    .subtract(ema[i - 1])
                    .multiply(k)
                    .add(ema[i - 1]);
        }
        // для i < period-1 заполняем первым доступным значением
        for (int i = 0; i < period - 1; i++) {
            ema[i] = ema[period - 1];
        }
        return ema;
    }
}
