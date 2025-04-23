package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.LstmConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import com.chicu.neurotradebot.ai.strategy.ml.PricePredictor;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LstmForecastStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final PricePredictor predictor;

    private LstmConfig config = new LstmConfig();

    @Override
    public String getName() {
        return "LSTM Forecast";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getWindowSize()) {
            log.warn("📉 Недостаточно данных для LSTM стратегии");
            return Signal.HOLD;
        }

        List<Double> history = new ArrayList<>();
        for (int i = candles.size() - config.getWindowSize(); i < candles.size(); i++) {
            history.add(candles.get(i).getClose());
        }

        double forecast = predictor.predictNextPrice(history);
        double lastPrice = candles.get(candles.size() - 1).getClose();

        log.info("🤖 LSTM: прогноз={}, текущая цена={}", forecast, lastPrice);

        if (forecast > lastPrice * 1.002) return Signal.BUY;
        if (forecast < lastPrice * 0.998) return Signal.SELL;
        return Signal.HOLD;
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof LstmConfig lstmConfig) {
            this.config = lstmConfig;
        } else {
            log.warn("❌ Неверная конфигурация передана в LSTM стратегию: {}", config);
        }
    }
}
