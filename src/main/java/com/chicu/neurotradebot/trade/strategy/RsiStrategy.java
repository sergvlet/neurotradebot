package com.chicu.neurotradebot.trade.strategy;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class RsiStrategy implements TradingStrategy {

    @Override
    public Signal generateSignal(String symbol,
                                 List<Bar> history,
                                 AiTradeSettings settings) {
        RsiMacdConfig cfg = settings.getRsiMacdConfig();
        int period = cfg.getRsiPeriod();
        BigDecimal lower = cfg.getRsiLower();
        BigDecimal upper = cfg.getRsiUpper();

        // собираем цены закрытия
        BigDecimal[] closes = history.stream()
                .map(Bar::getClose)
                .toArray(BigDecimal[]::new);
        BigDecimal rsi = calculateRSI(closes, period);

        if (rsi.compareTo(lower) < 0)  return Signal.BUY;
        if (rsi.compareTo(upper) > 0)  return Signal.SELL;
        return Signal.HOLD;
    }

    @Override
    public int requiredBars(AiTradeSettings settings) {
        // Для RSI нужен минимум period+1 цены
        int period = settings.getRsiMacdConfig().getRsiPeriod();
        return period + 1;
    }

    private BigDecimal calculateRSI(BigDecimal[] data, int period) {
        BigDecimal gain = BigDecimal.ZERO, loss = BigDecimal.ZERO;
        // первый расчёт: суммируем первые изменения
        for (int i = 1; i <= period; i++) {
            BigDecimal delta = data[i].subtract(data[i - 1]);
            if (delta.signum() > 0) gain = gain.add(delta);
            else                    loss = loss.add(delta.abs());
        }
        BigDecimal avgGain = gain.divide(
                BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        BigDecimal avgLoss = loss.divide(
                BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);

        // скользящее усреднение
        for (int i = period + 1; i < data.length; i++) {
            BigDecimal delta = data[i].subtract(data[i - 1]);
            BigDecimal thisGain = delta.max(BigDecimal.ZERO);
            BigDecimal thisLoss = delta.min(BigDecimal.ZERO).abs();

            avgGain = avgGain.multiply(BigDecimal.valueOf(period - 1))
                    .add(thisGain)
                    .divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
            avgLoss = avgLoss.multiply(BigDecimal.valueOf(period - 1))
                    .add(thisLoss)
                    .divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        }

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        BigDecimal rs = avgGain.divide(avgLoss, 8, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100)
                .subtract(BigDecimal.valueOf(100)
                        .divide(rs.add(BigDecimal.ONE), 8, RoundingMode.HALF_UP));
    }
}
