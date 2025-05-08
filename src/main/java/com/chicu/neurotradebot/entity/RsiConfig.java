// src/main/java/com/chicu/neurotradebot/entity/RsiConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Конфигурация стратегии RSI */
@Entity
@Table(name = "rsi_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RsiConfig extends BaseStrategyConfig {
    @Column(nullable = false)
    @Builder.Default
    private int period = 14;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal lower = BigDecimal.valueOf(30);

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal upper = BigDecimal.valueOf(70);
}
