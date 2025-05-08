// src/main/java/com/chicu/neurotradebot/entity/DcaConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Конфигурация стратегии DCA */
@Entity
@Table(name = "dca_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DcaConfig extends BaseStrategyConfig {
    @Column(name = "order_count", nullable = false)
    @Builder.Default
    private int orderCount = 5;

    @Column(name = "amount_per_order", nullable = false, precision = 19, scale = 8)
    @Builder.Default
    private BigDecimal amountPerOrder = BigDecimal.ZERO;
}
