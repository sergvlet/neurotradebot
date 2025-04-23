package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.EmaConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmaStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private EmaConfig config = new EmaConfig(); // по умолчанию

    @Override
    public String getName() {
        return "EMA (" + config.getShortPeriod() + "/" + config.getLongPeriod() + ")";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getLongPeriod()) {
            log.warn("📉 Недостаточно данных для анализа EMA");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        EMAIndicator shortEma = new EMAIndicator(closePrice, config.getShortPeriod());
        EMAIndicator longEma = new EMAIndicator(closePrice, config.getLongPeriod());

        int lastIndex = series.getEndIndex();
        double shortValue = shortEma.getValue(lastIndex).doubleValue();
        double longValue = longEma.getValue(lastIndex).doubleValue();

        log.info("📊 EMA анализ: short={} long={}", shortValue, longValue);

        if (shortValue > longValue) return Signal.BUY;
        else if (shortValue < longValue) return Signal.SELL;
        else return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof EmaConfig casted) {
            this.config = casted;
        } else {
            log.warn("❌ Некорректная конфигурация для EmaStrategy: {}", config);
        }
    }
}
