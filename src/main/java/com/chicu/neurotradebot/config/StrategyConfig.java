// src/main/java/com/chicu/neurotradebot/config/StrategyConfig.java
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
            RsiStrategy rsiStrategy,
            MacdStrategy macdStrategy,
            EmaCrossoverStrategy emaCrossoverStrategy

    ) {
        return Map.of(
                StrategyType.RSI,            rsiStrategy,
                StrategyType.MACD,           macdStrategy,
                StrategyType.EMA_CROSSOVER,  emaCrossoverStrategy

        );
    }
}
