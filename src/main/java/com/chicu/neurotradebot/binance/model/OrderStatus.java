package com.chicu.neurotradebot.binance.model;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Уникальный идентификатор записи ордера в базе данных

    @Column(name = "order_id", nullable = false)
    private String orderId;  // Идентификатор ордера

    @Column(name = "symbol", nullable = false)
    private String symbol;  // Символ актива (например, "BTCUSDT")

    @Column(name = "side", nullable = false)
    private String side;  // Направление ордера (BUY или SELL)

    @Column(name = "type", nullable = false)
    private String type;  // Тип ордера (LIMIT, MARKET и т.д.)

    @Column(name = "time_in_force", nullable = false)
    private String timeInForce;  // Время действия ордера (например, "GTC", "IOC", "FOK")

    @Column(name = "price", nullable = false)
    private BigDecimal price;  // Цена актива в ордере

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;  // Количество актива в ордере

    @Column(name = "executed_quantity")
    private BigDecimal executedQuantity;  // Количество исполненных ордеров

    @Column(name = "commission")
    private BigDecimal commission;  // Комиссия за исполнение ордера

    @Column(name = "commission_asset")
    private String commissionAsset;  // Актив, которым уплачивается комиссия (например, "USDT", "BNB")

    @Column(name = "status", nullable = false)
    private String status;  // Статус ордера (например, "FILLED", "PENDING", "CANCELED")

    @Column(name = "time")
    private Long time;  // Время исполнения ордера (timestamp)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private ActiveTrade activeTrade;  // Связь с активной сделкой (если она есть)

    @PrePersist
    protected void onCreate() {
        if (this.time == null) {
            this.time = System.currentTimeMillis();  // Если время не задано, ставим текущее время
        }
    }
}
