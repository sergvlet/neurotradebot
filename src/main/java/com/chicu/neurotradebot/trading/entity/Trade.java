package com.chicu.neurotradebot.trading.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TradingSession session;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private TradeSide side;

    private BigDecimal quantity;

    private BigDecimal price;

    private LocalDateTime createdAt;

    public enum TradeSide {
        BUY,
        SELL
    }
}
