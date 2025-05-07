package com.chicu.neurotradebot.trade.strategy;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RsiMacdStrategy implements TradingStrategy {

    @Override
    public Signal generateSignal(String symbol,
                                 List<Bar> history,
                                 AiTradeSettings settings) {
        if (settings == null) {
            throw new IllegalStateException("Нет настроек трейдера");
        }

        RsiMacdConfig cfg = settings.getRsiMacdConfig();
        if (cfg == null) {
            // Без конфигурации — не торгуем
            return Signal.HOLD;
        }

        int fastPeriod   = cfg.getMacdFast();
        int slowPeriod   = cfg.getMacdSlow();
        int signalPeriod = cfg.getMacdSignal();
        int rsiPeriod    = cfg.getRsiPeriod(); // если есть

        // 1) Собираем в массив цены закрытия:
        BigDecimal[] closes = history.stream()
                .map(Bar::getClose)
                .toArray(BigDecimal[]::new);

        // 2) Вычисляем EMA(fast) и EMA(slow):
        BigDecimal[] emaFast = calculateEMA(closes, fastPeriod);
        BigDecimal[] emaSlow = calculateEMA(closes, slowPeriod);

        // 3) Получаем MACD-линии:
        BigDecimal[] macdLine = new BigDecimal[emaFast.length];
        for (int i = 0; i < emaFast.length; i++) {
            macdLine[i] = emaFast[i].subtract(emaSlow[i]);
        }

        // 4) Сигнальная линия — EMA от MACD-линии:
        BigDecimal[] signalLine = calculateEMA(macdLine, signalPeriod);

        // 5) Берём последние два значения, чтобы определить кроссовер:
        int idx = macdLine.length - 1;
        BigDecimal prevMacd   = macdLine[idx - 1];
        BigDecimal prevSignal = signalLine[idx - 1];
        BigDecimal currMacd   = macdLine[idx];
        BigDecimal currSignal = signalLine[idx];

        // 6) Простое правило «пересечения»:
        if (prevMacd.compareTo(prevSignal) <= 0 && currMacd.compareTo(currSignal) > 0) {
            return Signal.BUY;
        }
        if (prevMacd.compareTo(prevSignal) >= 0 && currMacd.compareTo(currSignal) < 0) {
            return Signal.SELL;
        }

        // 7) (Опционально) добавьте RSI-фильтр:
        // BigDecimal rsi = calculateRSI(closes, rsiPeriod);
        // if (rsi.compareTo(cfg.getRsiOverbought()) > 0)  → SELL
        // if (rsi.compareTo(cfg.getRsiOversold())   < 0)  → BUY

        return Signal.HOLD;
    }

    private BigDecimal[] calculateEMA(BigDecimal[] data, int period) {
        BigDecimal[] ema = new BigDecimal[data.length];
        BigDecimal k = BigDecimal.valueOf(2.0 / (period + 1));
        // 1) Инициализация — простое среднее первых `period` баров:
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = sum.add(data[i]);
        }
        ema[period - 1] = sum.divide(BigDecimal.valueOf(period), BigDecimal.ROUND_HALF_UP);
        // 2) Рекуррентная формула:
        for (int i = period; i < data.length; i++) {
            ema[i] = data[i]
                    .subtract(ema[i - 1])
                    .multiply(k)
                    .add(ema[i - 1]);
        }
        return ema;
    }

    // private BigDecimal[] calculateEMA(BigDecimal[] ...) — перегрузка для BigDecimal[]
    // private BigDecimal calculateRSI(BigDecimal[] data, int period) { ... }
}
