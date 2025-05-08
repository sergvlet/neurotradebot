// src/main/java/com/chicu/neurotradebot/entity/ScalpingConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Конфигурация стратегии Scalping по стакану */
@Entity
@Table(name = "scalping_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScalpingConfig extends BaseStrategyConfig {
    @Column(name = "order_book_depth", nullable = false)
    @Builder.Default
    private int orderBookDepth = 5;

    @Column(name = "profit_threshold", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal profitThreshold = BigDecimal.valueOf(0.5);

    @Column(name = "stop_loss_threshold", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal stopLossThreshold = BigDecimal.valueOf(0.2);
}
