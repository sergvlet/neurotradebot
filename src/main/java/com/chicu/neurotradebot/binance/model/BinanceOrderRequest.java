package com.chicu.neurotradebot.binance.model;

import com.chicu.neurotradebot.telegramm.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "binance_order_request")
public class BinanceOrderRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Идентификатор ордера (если требуется для сохранения в базе)

    @Column(nullable = false)
    private String symbol;  // Символ актива (например, "BTCUSDT")

    @Column(nullable = false)
    private String side;  // Направление ордера: "BUY" или "SELL"

    @Column(nullable = false)
    private String type;  // Тип ордера: "LIMIT", "MARKET", "STOP_LIMIT", и т.д.

    @Column(nullable = false)
    private BigDecimal quantity;  // Количество актива для покупки/продажи

    @Column(nullable = true)
    private BigDecimal price;  // Цена актива для лимитного ордера

    @Column(nullable = true)
    private BigDecimal stopPrice;  // Цена стоп-лосса (если применяется)

    @Column(nullable = true)
    private String timeInForce;  // Время действия ордера: "GTC", "FOK", "IOC"

    @Column(nullable = false)
    private LocalDateTime timestamp;  // Время создания ордера

    @Column(nullable = true)
    private String status;  // Статус ордера: "FILLED", "PENDING", "CANCELED"

    // Дополнительные поля для хранения информации о пользователе или API ключе, если необходимо
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Связь с пользователем, который создал ордер (если требуется)
    
    // Возможность добавления других сущностей, например, для обработки ошибок или дополнительных деталей ордера
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_report_id", nullable = true)
    private ExecutionReport executionReport;  // Связь с отчетом о выполнении ордера (если требуется)

}
