package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_commissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_order_id")
    private TradeOrder tradeOrder; // Связь с ордером, к которому относится комиссия

    private String commissionAsset; // Актив, на который начисляется комиссия (например, BTC)
    private BigDecimal commissionAmount; // Сумма комиссии
    private BigDecimal commissionRate; // Ставка комиссии (в процентах)

}
