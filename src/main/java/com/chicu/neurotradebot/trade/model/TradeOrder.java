package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.binance.model.OrderSide;
import com.chicu.neurotradebot.binance.model.OrderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String symbol;
    private String exchange;
    private boolean demo;
    private String strategy;

    @Enumerated(EnumType.STRING)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    private String quantity;
    private double price;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<TradeExecution> executions;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderCommission> commissions;
}
