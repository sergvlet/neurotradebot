package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.ai.strategy.config.*;
import com.chicu.neurotradebot.trade.model.StrategyConfigEntity;
import com.chicu.neurotradebot.trade.repository.StrategyConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyConfigService {

    private final StrategyConfigRepository repository;
    private final ObjectMapper objectMapper;

    public StrategyConfig getConfig(Long chatId, AvailableStrategy strategy) {
        return repository.findByChatIdAndStrategy(chatId, strategy)
                .map(entity -> fromJson(entity.getConfigJson(), strategy))
                .orElseGet(() -> defaultConfig(strategy));
    }

    public void saveConfig(Long chatId, AvailableStrategy strategy, StrategyConfig config) {
        try {
            String json = objectMapper.writeValueAsString(config);

            StrategyConfigEntity entity = repository.findByChatIdAndStrategy(chatId, strategy)
                    .orElse(new StrategyConfigEntity());

            entity.setChatId(chatId);
            entity.setStrategy(strategy);
            entity.setConfigJson(json);
            repository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("❌ Ошибка сериализации конфигурации", e);
        }
    }

    private StrategyConfig fromJson(String json, AvailableStrategy strategy) {
        try {
            return switch (strategy) {
                case SMA -> objectMapper.readValue(json, SmaConfig.class);
                case EMA -> objectMapper.readValue(json, EmaConfig.class);
                case MACD -> objectMapper.readValue(json, MacdConfig.class);
                case RSI -> objectMapper.readValue(json, RsiConfig.class);
                case STOCH_RSI -> objectMapper.readValue(json, StochasticRsiConfig.class);
                case BOLLINGER_BANDS -> objectMapper.readValue(json, BollingerBandsConfig.class);
                case ADX -> objectMapper.readValue(json, AdxConfig.class);
                case ICHIMOKU -> objectMapper.readValue(json, IchimokuConfig.class);
                case CCI -> objectMapper.readValue(json, CciConfig.class);
                case DONCHIAN_CHANNEL -> objectMapper.readValue(json, DonchianChannelConfig.class);
                case OBV -> objectMapper.readValue(json, ObvConfig.class);
                case VWAP -> objectMapper.readValue(json, VwapConfig.class);
                case LSTM -> objectMapper.readValue(json, LstmConfig.class);
                case XGBOOST -> objectMapper.readValue(json, XgboostConfig.class);
                case HYBRID -> objectMapper.readValue(json, HybridAiConfig.class);
            };
        } catch (Exception e) {
            throw new RuntimeException("❌ Ошибка десериализации конфигурации", e);
        }
    }

    private StrategyConfig defaultConfig(AvailableStrategy strategy) {
        return switch (strategy) {
            case SMA -> new SmaConfig();
            case EMA -> new EmaConfig();
            case MACD -> new MacdConfig();
            case RSI -> new RsiConfig();
            case STOCH_RSI -> new StochasticRsiConfig();
            case BOLLINGER_BANDS -> new BollingerBandsConfig();
            case ADX -> new AdxConfig();
            case ICHIMOKU -> new IchimokuConfig();
            case CCI -> new CciConfig();
            case DONCHIAN_CHANNEL -> new DonchianChannelConfig();
            case OBV -> new ObvConfig();
            case VWAP -> new VwapConfig();
            case LSTM -> new LstmConfig();
            case XGBOOST -> new XgboostConfig();
            case HYBRID -> new HybridAiConfig();
        };
    }
}
