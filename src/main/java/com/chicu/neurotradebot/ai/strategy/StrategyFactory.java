package com.chicu.neurotradebot.ai.strategy;

import com.chicu.neurotradebot.ai.strategy.impl.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StrategyFactory {

    private final SmaStrategy smaStrategy;
    private final EmaStrategy emaStrategy;
    private final MacdStrategy macdStrategy;
    private final RsiStrategy rsiStrategy;
    private final StochasticRsiStrategy stochasticRsiStrategy;
    private final BollingerBandsStrategy bollingerBandsStrategy;
    private final AdxStrategy adxStrategy;
    private final IchimokuStrategy ichimokuStrategy;
    private final CciStrategy cciStrategy;
    private final DonchianChannelStrategy donchianChannelStrategy;
    private final ObvStrategy obvStrategy;
    private final VwapStrategy vwapStrategy;
    private final LstmForecastStrategy lstmForecastStrategy;
    private final XgboostSignalStrategy xgboostSignalStrategy;
    private final HybridAiStrategy hybridAiStrategy;

    private final Map<AvailableStrategy, AiStrategy> strategies = new EnumMap<>(AvailableStrategy.class);

    @PostConstruct
    public void init() {
        strategies.put(AvailableStrategy.SMA, smaStrategy);
        strategies.put(AvailableStrategy.EMA, emaStrategy);
        strategies.put(AvailableStrategy.MACD, macdStrategy);
        strategies.put(AvailableStrategy.RSI, rsiStrategy);
        strategies.put(AvailableStrategy.STOCH_RSI, stochasticRsiStrategy);
        strategies.put(AvailableStrategy.BOLLINGER_BANDS, bollingerBandsStrategy);
        strategies.put(AvailableStrategy.ADX, adxStrategy);
        strategies.put(AvailableStrategy.ICHIMOKU, ichimokuStrategy);
        strategies.put(AvailableStrategy.CCI, cciStrategy);
        strategies.put(AvailableStrategy.DONCHIAN_CHANNEL, donchianChannelStrategy);
        strategies.put(AvailableStrategy.OBV, obvStrategy);
        strategies.put(AvailableStrategy.VWAP, vwapStrategy);
        strategies.put(AvailableStrategy.LSTM, lstmForecastStrategy);
        strategies.put(AvailableStrategy.XGBOOST, xgboostSignalStrategy);
        strategies.put(AvailableStrategy.HYBRID, hybridAiStrategy);
    }

    /**
     * Получение стратегии по умолчанию или указанной
     * @param strategy Стратегия из перечисления AvailableStrategy
     * @return Соответствующая стратегия
     */
    public AiStrategy get(AvailableStrategy strategy) {
        AiStrategy aiStrategy = strategies.get(strategy);
        if (aiStrategy == null) {
            throw new IllegalArgumentException("Стратегия не найдена: " + strategy);
        }
        return aiStrategy;
    }

    /**
     * Получение всех стратегий на основе выбранных пользователем
     * @param selected Стратегии, выбранные пользователем
     * @return Словарь с соответствующими стратегиями
     */
    public Map<AvailableStrategy, AiStrategy> getStrategies(Set<AvailableStrategy> selected) {
        Map<AvailableStrategy, AiStrategy> result = new EnumMap<>(AvailableStrategy.class);
        for (AvailableStrategy s : selected) {
            AiStrategy impl = strategies.get(s);
            if (impl != null) {
                result.put(s, impl);
            } else {
                throw new IllegalArgumentException("Стратегия не найдена для: " + s);
            }
        }
        return result;
    }
}
