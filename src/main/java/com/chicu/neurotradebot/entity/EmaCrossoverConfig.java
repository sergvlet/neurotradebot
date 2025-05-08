// src/main/java/com/chicu/neurotradebot/entity/EmaCrossoverConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

/** Конфигурация стратегии EMA Crossover */
@Entity
@Table(name = "ema_crossover_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmaCrossoverConfig extends BaseStrategyConfig {
    @Column(name = "short_period", nullable = false)
    @Builder.Default
    private int shortPeriod = 9;

    @Column(name = "long_period", nullable = false)
    @Builder.Default
    private int longPeriod  = 21;
}
