package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.AdxConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.model.MarketCandle;
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
    private AdxConfig config = new AdxConfig(); // По умолчанию

    @Override
    public String getName() {
        return "ADX";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod() + 1) {
            log.warn("📉 Недостаточно данных для ADX (нужно минимум {} баров)", config.getPeriod() + 1);
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ADXIndicator adx = new ADXIndicator(series, config.getPeriod());

        double value = adx.getValue(series.getEndIndex()).doubleValue();

        log.info("📊 ADX: value={}, threshold={}", value, config.getTrendStrengthThreshold());

        return value > config.getTrendStrengthThreshold() ? Signal.BUY : Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof AdxConfig) {
            this.config = (AdxConfig) config;
        } else {
            log.warn("⚠️ Неверный тип конфигурации для ADX: {}", config);
        }
    }
}
