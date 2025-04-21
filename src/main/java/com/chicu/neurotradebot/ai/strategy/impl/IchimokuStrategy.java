package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.IchimokuConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.ichimoku.IchimokuKijunSenIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuTenkanSenIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IchimokuStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final IchimokuConfig config = new IchimokuConfig();

    @Override
    public String getName() {
        return "Ichimoku";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getBaseLinePeriod() + 1) {
            log.warn("📉 Недостаточно данных для Ichimoku (минимум {} баров)", config.getBaseLinePeriod() + 1);
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);

        IchimokuTenkanSenIndicator tenkanSen = new IchimokuTenkanSenIndicator(series, config.getConversionLinePeriod());
        IchimokuKijunSenIndicator kijunSen = new IchimokuKijunSenIndicator(series, config.getBaseLinePeriod());
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        int lastIndex = series.getEndIndex();
        double tenkan = tenkanSen.getValue(lastIndex).doubleValue();
        double kijun = kijunSen.getValue(lastIndex).doubleValue();
        double price = close.getValue(lastIndex).doubleValue();

        log.info("📊 Ichimoku: price={}, tenkan={}, kijun={}", price, tenkan, kijun);

        if (tenkan > kijun && price > tenkan) {
            return Signal.BUY;
        } else if (tenkan < kijun && price < tenkan) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }
}
