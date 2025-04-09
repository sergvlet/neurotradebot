package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_execution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tradeOrderId;

    private BigDecimal executedPrice;

    private BigDecimal executedQuantity;

    private LocalDateTime executionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TradeOrder tradeOrder;
}
