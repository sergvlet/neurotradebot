package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.XgboostConfig;
import com.chicu.neurotradebot.ai.strategy.ml.SignalClassifier;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class XgboostSignalStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private final SignalClassifier classifier;
    private final XgboostConfig config = new XgboostConfig();

    @Override
    public String getName() {
        return "XGBoost Signal";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getHistorySize()) {
            log.warn("📉 Недостаточно данных для XGBoost стратегии");
            return Signal.HOLD;
        }

        List<Double> history = new ArrayList<>();
        for (int i = candles.size() - config.getHistorySize(); i < candles.size(); i++) {
            history.add(candles.get(i).getClose());
        }

        Signal signal = classifier.classify(history);
        log.info("🧠 XGBoost сигнал: {}", signal);
        return signal;
    }
}
