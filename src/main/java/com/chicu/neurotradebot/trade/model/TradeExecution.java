package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_order_id")
    private TradeOrder tradeOrder; // Связь с ордером, к которому относится выполнение

    private LocalDateTime executionTime; // Время исполнения ордера
    private BigDecimal executedQuantity; // Количество выполненной сделки
    private BigDecimal executedPrice; // Цена, по которой был выполнен ордер

    private String status; // Статус исполнения (например, "FILLED", "PENDING")

}
