package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_commission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tradeOrderId;

    private BigDecimal commission;

    private String commissionAsset;

    private String commissionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TradeOrder tradeOrder;
}
