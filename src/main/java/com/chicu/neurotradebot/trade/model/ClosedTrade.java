package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "closed_trade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosedTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String symbol;

    private String exchange;

    private boolean demo;

    private String strategy;

    private BigDecimal quantity;

    private BigDecimal entryPrice;

    private BigDecimal exitPrice;

    private BigDecimal profit;

    private LocalDateTime openTime;

    private LocalDateTime closeTime;
}
