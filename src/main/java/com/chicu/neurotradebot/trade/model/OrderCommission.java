package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asset;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private TradeOrder order;
}
