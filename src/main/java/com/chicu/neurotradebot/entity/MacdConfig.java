// src/main/java/com/chicu/neurotradebot/entity/MacdConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

/** Конфигурация стратегии MACD */
@Entity
@Table(name = "macd_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MacdConfig extends BaseStrategyConfig {
    @Column(nullable = false)
    @Builder.Default
    private int fast   = 12;

    @Column(nullable = false)
    @Builder.Default
    private int slow   = 26;

    @Column(nullable = false)
    @Builder.Default
    private int signal = 9;
}
