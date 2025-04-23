package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.SmaConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmaStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private SmaConfig config = new SmaConfig(5, 20);

    @Override
    public String getName() {
        return "SMA (" + config.getShortPeriod() + "/" + config.getLongPeriod() + ")";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getLongPeriod()) {
            log.warn("📉 Недостаточно данных для анализа SMA");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        SMAIndicator shortSma = new SMAIndicator(closePrice, config.getShortPeriod());
        SMAIndicator longSma = new SMAIndicator(closePrice, config.getLongPeriod());

        int lastIndex = series.getEndIndex();

        double shortValue = shortSma.getValue(lastIndex).doubleValue();
        double longValue = longSma.getValue(lastIndex).doubleValue();

        log.info("📊 SMA анализ: short={} long={}", shortValue, longValue);

        if (shortValue > longValue) return Signal.BUY;
        else if (shortValue < longValue) return Signal.SELL;
        else return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof SmaConfig smaConfig) {
            this.config = smaConfig;
        } else {
            log.warn("❌ Неверная конфигурация для SMA стратегии: {}", config);
        }
    }
}
