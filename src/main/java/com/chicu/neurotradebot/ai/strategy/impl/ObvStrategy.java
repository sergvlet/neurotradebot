package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.ObvConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObvStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final ObvConfig config = new ObvConfig();

    @Override
    public String getName() {
        return "OBV";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getMinCandles()) {
            log.warn("📉 Недостаточно данных для OBV стратегии");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        int end = series.getEndIndex();
        if (end < config.getLookbackPeriod()) return Signal.HOLD;

        double currentObv = obv.getValue(end).doubleValue();
        double previousObv = obv.getValue(end - config.getLookbackPeriod()).doubleValue();
        double currentClose = close.getValue(end).doubleValue();
        double previousClose = close.getValue(end - config.getLookbackPeriod()).doubleValue();

        log.info("📊 OBV: current={}, previous={}, closeDiff={}", currentObv, previousObv, currentClose - previousClose);

        if (currentObv > previousObv && currentClose > previousClose) {
            return Signal.BUY;
        } else if (currentObv < previousObv && currentClose < previousClose) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }
}
