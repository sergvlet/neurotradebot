package com.chicu.neurotradebot.binance.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "execution_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Поле для хранения уникального идентификатора ордера
    private String orderId;

    // Статус ордера, например "FILLED", "PARTIALLY_FILLED", "CANCELED"
    private String status;

    // Время исполнения отчета
    private LocalDateTime timestamp;

    // Количество ордера, которое было исполнено
    private BigDecimal executedQuantity;

    // Цена исполнения
    private BigDecimal executedPrice;

    // Тип ордера, например "LIMIT", "MARKET"
    private String orderType;

    // Направление ордера: "BUY" или "SELL"
    private String side;

    // Сумма, которая была потрачена или получена по ордеру
    private BigDecimal totalAmount;

    // Статус исполнения, например "SUCCESS", "FAILED"
    private String executionStatus;

    // Валюта, в которой исполнился ордер
    private String currency;

    // Обновление времени исполнения, если ордер частично исполнился
    private LocalDateTime updateTimestamp;

    // Связь с соответствующим ордером
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "binance_order_id")
    private BinanceOrderRequest binanceOrderRequest;
}
