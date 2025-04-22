package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.RsiConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
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
    private final RsiConfig config = new RsiConfig();

    @Override
    public String getName() {
        return "RSI";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod() + 1) {
            log.warn("📉 Недостаточно данных для RSI анализа");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsi = new RSIIndicator(closePrice, config.getPeriod());

        int lastIndex = series.getEndIndex();
        double rsiValue = rsi.getValue(lastIndex).doubleValue();

        log.info("📊 RSI = {}", rsiValue);

        if (rsiValue < config.getOversold()) {
            return Signal.BUY;
        } else if (rsiValue > config.getOverbought()) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }
}
