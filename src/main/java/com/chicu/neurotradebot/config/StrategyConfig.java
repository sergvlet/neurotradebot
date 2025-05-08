package com.chicu.neurotradebot.config;

import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import com.chicu.neurotradebot.trade.strategy.EmaCrossoverStrategy;
import com.chicu.neurotradebot.trade.strategy.MacdStrategy;
import com.chicu.neurotradebot.trade.strategy.RsiStrategy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class StrategyConfig {

    @Bean
    public Map<StrategyType, TradingStrategy> strategyMap(
            RsiStrategy rsi,
            MacdStrategy macd,
            EmaCrossoverStrategy ema
    ) {
        return Map.of(
            StrategyType.RSI,           rsi,
            StrategyType.MACD,          macd,
            StrategyType.EMA_CROSSOVER, ema
        );
    }
}
