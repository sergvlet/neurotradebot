package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "market_candle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol; // Торговая пара (например, BTCUSDT)

    @Column(nullable = false)
    private String interval; // Интервал (например, 1m, 5m, 1h)

    @Column(nullable = false)
    private ZonedDateTime timestamp; // Время открытия свечи

    @Column(nullable = false)
    private double open;

    @Column(nullable = false)
    private double high;

    @Column(nullable = false)
    private double low;

    @Column(nullable = false)
    private double close;

    @Column(nullable = false)
    private double volume;
}
