package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.RsiConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RsiStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final RsiConfig config = new RsiConfig(14); // Можно внедрять через Spring, если нужно

    @Override
    public String getName() {
        return "RSI (" + config.getPeriod() + ")";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod() + 1) {
            log.warn("📉 Недостаточно данных для анализа RSI");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(closePrice, config.getPeriod());

        int lastIndex = series.getEndIndex();
        double rsiValue = rsi.getValue(lastIndex).doubleValue();

        log.info("📊 RSI: {}", rsiValue);

        if (rsiValue < 30) {
            return Signal.BUY;
        } else if (rsiValue > 70) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }
}
