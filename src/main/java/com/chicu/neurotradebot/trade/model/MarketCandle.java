package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol; // торговая пара, например, BTCUSDT
    private String interval; // интервал свечи, например, 1m, 5m, 1h
    private Instant timestamp; // время открытия свечи
    private double open; // цена открытия
    private double high; // высокая цена
    private double low; // низкая цена
    private double close; // цена закрытия
    private double volume; // объем
    private Instant closeTime; // время закрытия свечи

    // Убедитесь, что openTime правильно инициализируется в конструкторе
    public MarketCandle(String symbol, String interval, Instant openTime, double open, double high, double low, double close, double volume, Instant closeTime) {
        this.symbol = symbol;
        this.interval = interval;
        this.timestamp = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.closeTime = closeTime;
    }
}
