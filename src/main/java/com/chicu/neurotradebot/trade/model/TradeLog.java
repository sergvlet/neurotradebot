package com.chicu.neurotradebot.trade.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_trade_id")
    private ActiveTrade activeTrade; // Связь с активной сделкой, к которой относится лог

    private LocalDateTime timestamp; // Время записи лога
    private String action; // Тип действия, например, "BUY", "SELL", "CLOSE"
    private BigDecimal price; // Цена актива, на которой было выполнено действие
    private BigDecimal quantity; // Количество актива в сделке
    private BigDecimal totalAmount; // Общая сумма сделки
    private String status; // Статус сделки, например "SUCCESS", "FAIL"

}
