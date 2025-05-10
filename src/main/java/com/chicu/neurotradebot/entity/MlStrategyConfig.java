package com.chicu.neurotradebot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MlStrategyConfig {

    /** Общий капитал для ML-стратегии (USDT) */
    @Column(
            name = "ml_total_capital_usd",
            nullable = false,
            columnDefinition = "numeric(38,2) DEFAULT 100.00"
    )
    private BigDecimal totalCapitalUsd;

    /** Период анализа (например, PT720H = 30 суток) */
    @Column(name = "ml_lookback_period", nullable = false)
    private Duration lookbackPeriod;

    /** URL REST-сервиса с ML-моделью */
    @Column(
            name = "ml_predict_url",
            nullable = false,
            columnDefinition = "varchar(255) DEFAULT 'http://localhost:5000/predict'"
    )
    private String predictUrl;

    /** Порог RSI для входа (по умолчанию 32) */
    @Column(name = "ml_entry_rsi_threshold", nullable = false)
    @Builder.Default
    private double entryRsiThreshold = 32.0;
}
