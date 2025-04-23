package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.StochasticRsiConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.StochasticRSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StochasticRsiStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private StochasticRsiConfig config = new StochasticRsiConfig();

    @Override
    public String getName() {
        return "Stochastic RSI";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getRsiPeriod() + config.getStochasticPeriod()) {
            log.warn("📉 Недостаточно данных для анализа Stochastic RSI");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        StochasticRSIIndicator stochasticRsi = new StochasticRSIIndicator(closePrice, config.getRsiPeriod());

        int lastIndex = series.getEndIndex();
        double stochasticRsiValue = stochasticRsi.getValue(lastIndex).doubleValue();

        log.info("📊 Stochastic RSI: {}", stochasticRsiValue);

        if (stochasticRsiValue < 0.2) return Signal.BUY;
        else if (stochasticRsiValue > 0.8) return Signal.SELL;
        else return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof StochasticRsiConfig stochasticConfig) {
            this.config = stochasticConfig;
        } else {
            log.warn("❌ Неверная конфигурация для Stochastic RSI стратегии: {}", config);
        }
    }
}
