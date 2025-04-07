package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosedTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String symbol;
    private String exchange;
    private boolean demo;

    private String strategy;

    private String quantity;
    private double openPrice;
    private double closePrice;

    private double profit;

    private LocalDateTime openTime;
    private LocalDateTime closeTime;
}
