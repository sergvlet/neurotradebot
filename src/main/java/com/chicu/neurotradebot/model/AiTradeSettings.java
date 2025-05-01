package com.chicu.neurotradebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ai_trade_settings")
@Getter
@Setter
@NoArgsConstructor
public class AiTradeSettings {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserTradingSettings userTradingSettings;

    // Основные AI-настройки
    private String strategy;                // Стратегия: "RSI_EMA"
    private String risk;                    // Риск: LOW / MEDIUM / HIGH
    private String tradingType;             // Тип торговли: spot и т.д.


    private Boolean notifications = false;

    // Режим выбора валюты
    private String pairMode;                // MANUAL / LIST / AUTO
    private String manualPair;              // Выбранная пара вручную

    @Column(length = 1000)
    private String allowedPairs;            // Списки пар через \n

    @Column(name = "is_running")
    private Boolean running = false;        // Флаг активности AI

    // Настройки RSI+EMA стратегии (гибкие)
    @Column(name = "min_candles")
    private Integer minCandles = 50;

    @Column(name = "ema_short")
    private Integer emaShort = 9;

    @Column(name = "ema_long")
    private Integer emaLong = 21;

    @Column(name = "rsi_period")
    private Integer rsiPeriod = 14;

    @Column(name = "rsi_buy_threshold")
    private Double rsiBuyThreshold = 30.0;

    @Column(name = "rsi_sell_threshold")
    private Double rsiSellThreshold = 70.0;
}
