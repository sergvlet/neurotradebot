package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_trade_id")
    private ActiveTrade activeTrade; // Связь с активной сделкой

    private LocalDateTime timestamp; // Время, когда был создан ордер
    private String orderType; // Тип ордера (например, "LIMIT", "MARKET")
    private String side; // Направление ордера: "BUY" или "SELL"
    private BigDecimal price; // Цена ордера
    private BigDecimal quantity; // Количество актива
    private BigDecimal totalAmount; // Общая сумма ордера (цена * количество)
    private String status; // Статус ордера (например, "FILLED", "PENDING", "CANCELED")
    private String exchange; // Биржа, на которой был создан ордер (например, "BINANCE")

}
