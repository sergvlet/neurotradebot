package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "strategy_config")
public class StrategyConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    @Enumerated(EnumType.STRING)
    private AvailableStrategy strategy;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String configJson;
}
