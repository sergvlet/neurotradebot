package com.chicu.neurotradebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "strategy_settings")
@Getter
@Setter
@NoArgsConstructor
public class StrategySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String strategyName;

    private int emaShort = 9;
    private int emaLong = 21;
    private int rsiPeriod = 14;
    private double rsiBuyThreshold = 30.0;
    private double rsiSellThreshold = 70.0;
    private int minCandles = 50;

    public void resetToDefault() {
        this.emaShort = 9;
        this.emaLong = 21;
        this.rsiPeriod = 14;
        this.rsiBuyThreshold = 30.0;
        this.rsiSellThreshold = 70.0;
        this.minCandles = 50;
    }
}
