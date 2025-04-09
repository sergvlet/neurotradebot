package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tradeExecutionId;

    private String symbol;

    private String side;  // BUY or SELL

    private BigDecimal price;

    private BigDecimal quantity;

    private LocalDateTime orderTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_execution_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TradeExecution tradeExecution;
}
