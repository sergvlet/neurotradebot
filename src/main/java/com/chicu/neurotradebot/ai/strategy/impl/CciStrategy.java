package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.CciConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CCIIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CciStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private CciConfig config = new CciConfig();

    @Override
    public String getName() {
        return "CCI";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod()) {
            log.warn("📉 Недостаточно данных для CCI");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        CCIIndicator cci = new CCIIndicator(series, config.getPeriod());

        int lastIndex = series.getEndIndex();
        double cciValue = cci.getValue(lastIndex).doubleValue();

        log.info("📊 CCI: {}", cciValue);

        if (cciValue < config.getOversoldThreshold()) return Signal.BUY;
        else if (cciValue > config.getOverboughtThreshold()) return Signal.SELL;
        else return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof CciConfig cciConfig) {
            this.config = cciConfig;
        } else {
            log.warn("❌ Неверная конфигурация для CciStrategy: {}", config);
        }
    }
}
