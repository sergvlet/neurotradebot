package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;

@Entity
@Data
public class ActiveTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String symbol;
    private String strategy;
    private String mode;

    private String side; //  BUY или SELL

    private String amount;      // в монетах (например: 0.01 BTC)
    private String usdtAmount;  // в USDT

    private double openPrice;
    private ZonedDateTime openTime = ZonedDateTime.now();
}
