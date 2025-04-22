package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.VwapConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.volume.VWAPIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VwapStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final VwapConfig config = new VwapConfig();

    @Override
    public String getName() {
        return "VWAP";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod()) {
            log.warn("📉 Недостаточно данных для VWAP стратегии");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        VWAPIndicator vwap = new VWAPIndicator(series, config.getPeriod());
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        int lastIndex = series.getEndIndex();
        double vwapValue = vwap.getValue(lastIndex).doubleValue();
        double closeValue = close.getValue(lastIndex).doubleValue();

        log.info("📊 VWAP: close={}, vwap={}", closeValue, vwapValue);

        if (closeValue > vwapValue) {
            return Signal.BUY;
        } else if (closeValue < vwapValue) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }
}
