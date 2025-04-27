package com.chicu.neurotradebot.strategy.entity;

import com.chicu.neurotradebot.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "strategy_configurations")
public class StrategyConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String strategyName;

    @Lob
    private String parameters; // JSON строка параметров

    private LocalDateTime createdAt;
}
