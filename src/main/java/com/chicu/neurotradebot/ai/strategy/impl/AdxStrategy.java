package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.AdxConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.adx.ADXIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdxStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final AdxConfig config = new AdxConfig(); // по умолчанию: period = 14, threshold = 20

    @Override
    public String getName() {
        return "ADX";
    }


    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod() + 1) {
            log.warn("📉 Недостаточно данных для расчета ADX (нужно минимум {} баров)", config.getPeriod() + 1);
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ADXIndicator adx = new ADXIndicator(series, config.getPeriod());

        int lastIndex = series.getEndIndex();
        double adxValue = adx.getValue(lastIndex).doubleValue();

        log.info("📊 ADX: value={}, threshold={}", adxValue, config.getTrendStrengthThreshold());

        if (adxValue > config.getTrendStrengthThreshold()) {
            return Signal.BUY;
        } else {
            return Signal.HOLD;
        }
    }
}
