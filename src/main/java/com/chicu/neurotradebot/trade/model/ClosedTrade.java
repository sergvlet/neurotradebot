package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;

@Entity
@Data
public class ClosedTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String symbol;
    private String strategy;
    private String mode;

    private String amount;
    private String usdtAmount;

    private double openPrice;
    private double closePrice;
    private double profit; // 💰 реальная прибыль

    private ZonedDateTime openTime;
    private ZonedDateTime closeTime = ZonedDateTime.now();
}
