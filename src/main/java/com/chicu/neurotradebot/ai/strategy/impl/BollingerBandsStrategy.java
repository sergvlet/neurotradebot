package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.BollingerBandsConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BollingerBandsStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private BollingerBandsConfig config = new BollingerBandsConfig(); // по умолчанию

    @Override
    public String getName() {
        return "Bollinger Bands";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod()) {
            log.warn("📉 Недостаточно данных для Bollinger Bands");
            return Signal.HOLD;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator sma = new SMAIndicator(closePrice, config.getPeriod());
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePrice, config.getPeriod());

        BollingerBandsMiddleIndicator middleBand = new BollingerBandsMiddleIndicator(sma);
        Num multiplier = series.numOf(config.getMultiplier());

        BollingerBandsUpperIndicator upperBand = new BollingerBandsUpperIndicator(middleBand, stdDev, multiplier);
        BollingerBandsLowerIndicator lowerBand = new BollingerBandsLowerIndicator(middleBand, stdDev, multiplier);

        int lastIndex = series.getEndIndex();
        Num close = closePrice.getValue(lastIndex);
        Num lower = lowerBand.getValue(lastIndex);
        Num upper = upperBand.getValue(lastIndex);

        log.info("📊 Bollinger Bands: close={}, upper={}, lower={}", close, upper, lower);

        if (close.isLessThan(lower)) return Signal.BUY;
        else if (close.isGreaterThan(upper)) return Signal.SELL;
        else return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config){
        if (config instanceof BollingerBandsConfig bollingerConfig) {
            this.config = bollingerConfig;
        } else {
            log.warn("❌ Неверная конфигурация передана в BollingerBandsStrategy: {}", config);
        }
    }
}
