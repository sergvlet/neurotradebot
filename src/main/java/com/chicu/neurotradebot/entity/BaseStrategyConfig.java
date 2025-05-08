// src/main/java/com/chicu/neurotradebot/entity/BaseStrategyConfig.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Общий базовый класс для конфигов стратегий.
 * PRIMARY KEY = ai_trade_settings.id
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseStrategyConfig {
    @Id
    @Column(name = "ai_trade_settings_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ai_trade_settings_id")
    private AiTradeSettings settings;
}
