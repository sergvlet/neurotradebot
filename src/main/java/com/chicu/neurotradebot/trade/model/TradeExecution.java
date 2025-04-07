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
public class TradeExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String executionId;
    private double price;
    private double quantity;
    private double commission;
    private String commissionAsset;
    private LocalDateTime executedAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private TradeOrder order;
}
