package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.DonchianChannelConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.*;


import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DonchianChannelStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final DonchianChannelConfig config = new DonchianChannelConfig();

    @Override
    public String getName() {
        return "Donchian Channel";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod()) {
            log.warn("📉 Недостаточно данных для Donchian Channel");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);

        HighPriceIndicator highPrice = new HighPriceIndicator(series);
        LowPriceIndicator lowPrice = new LowPriceIndicator(series);
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        HighestValueIndicator upper = new HighestValueIndicator(highPrice, config.getPeriod());
        LowestValueIndicator lower = new LowestValueIndicator(lowPrice, config.getPeriod());

        int lastIndex = series.getEndIndex();
        double closeValue = close.getValue(lastIndex).doubleValue();
        double upperValue = upper.getValue(lastIndex).doubleValue();
        double lowerValue = lower.getValue(lastIndex).doubleValue();

        log.info("📊 Donchian Channel: close={}, upper={}, lower={}", closeValue, upperValue, lowerValue);

        if (closeValue > upperValue) return Signal.BUY;
        else if (closeValue < lowerValue) return Signal.SELL;
        else return Signal.HOLD;
    }
}
