package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;

@Entity
@Data
public class TradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String symbol;
    private String strategy;
    private String signal; // BUY / SELL / HOLD
    private String mode; // REAL / DEMO

    private String details; // описание/расчёт/индикаторы

    private ZonedDateTime timestamp = ZonedDateTime.now();
}
