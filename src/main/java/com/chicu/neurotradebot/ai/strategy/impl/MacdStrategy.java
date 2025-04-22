package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.MacdConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MacdStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final MacdConfig config = new MacdConfig(); // можно заменить на @Value или @ConfigurationProperties

    @Override
    public String getName() {
        return "MACD (" + config.getShortPeriod() + "/" + config.getLongPeriod() + "/" + config.getSignalPeriod() + ")";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getLongPeriod() + config.getSignalPeriod()) {
            log.warn("📉 Недостаточно данных для анализа MACD");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        MACDIndicator macd = new MACDIndicator(closePrice, config.getShortPeriod(), config.getLongPeriod());
        EMAIndicator signalLine = new EMAIndicator(macd, config.getSignalPeriod());

        int lastIndex = series.getEndIndex();
        double macdValue = macd.getValue(lastIndex).doubleValue();
        double signalValue = signalLine.getValue(lastIndex).doubleValue();

        log.info("📊 MACD: {}, Signal: {}", macdValue, signalValue);

        if (macdValue > signalValue) {
            return Signal.BUY;
        } else if (macdValue < signalValue) {
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }
}
