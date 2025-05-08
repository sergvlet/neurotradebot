package com.chicu.neurotradebot.trade.strategy;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CombinedStrategy implements TradingStrategy {

    private final Map<StrategyType, TradingStrategy> strategies;

    @Override
    public Signal generateSignal(String symbol,
                                 List<Bar> history,
                                 AiTradeSettings settings) {
        boolean anyBuy = false;
        for (StrategyType type : settings.getStrategies()) {
            TradingStrategy strat = strategies.get(type);
            Signal s = strat.generateSignal(symbol, history, settings);
            if (s == Signal.SELL) {
                return Signal.SELL;
            }
            if (s == Signal.BUY) {
                anyBuy = true;
            }
        }
        return anyBuy ? Signal.BUY : Signal.HOLD;
    }

    @Override
    public int requiredBars(AiTradeSettings settings) {
        return settings.getStrategies().stream()
                .map(strategies::get)
                .map(strategy -> strategy.requiredBars(settings))
                .max(Comparator.naturalOrder())
                .orElse(0);
    }
}
