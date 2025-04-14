package com.chicu.neurotradebot.ai.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AvailableStrategy {
    SMA("SMA"),
    EMA("EMA"),
    MACD("MACD"),
    RSI("RSI"),
    STOCH_RSI("Stochastic RSI"),
    BOLLINGER_BANDS("Bollinger Bands"),
    ADX("ADX"),
    ICHIMOKU("Ichimoku"),
    CCI("CCI"),
    DONCHIAN_CHANNEL("Donchian Channel"),
    OBV("OBV"),
    VWAP("VWAP"),
    LSTM("LSTM Forecast"),
    XGBOOST("XGBoost Signal"),
    HYBRID("Hybrid AI");

    private final String title;
}
