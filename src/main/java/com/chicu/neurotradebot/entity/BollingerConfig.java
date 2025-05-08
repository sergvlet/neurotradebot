// src/main/java/com/chicu/neurotradebot/entity/BollingerConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Конфигурация стратегии Bollinger Bands */
@Entity
@Table(name = "bollinger_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BollingerConfig extends BaseStrategyConfig {
    @Column(nullable = false)
    @Builder.Default
    private int period = 20;

    @Column(name = "std_dev_multiplier", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal stdDevMultiplier = BigDecimal.valueOf(2);
}
