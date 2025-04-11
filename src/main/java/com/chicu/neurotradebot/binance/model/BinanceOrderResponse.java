package com.chicu.neurotradebot.binance.model;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "binance_order_response")
@Data
public class BinanceOrderResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Идентификатор записи в базе данных

    private String orderId;        // Идентификатор ордера
    private String symbol;         // Торговая пара (например, "BTCUSDT")
    private String status;         // Статус ордера (например, "FILLED", "PENDING", "CANCELED")
    private String side;           // Направление ордера ("BUY" или "SELL")
    private String type;           // Тип ордера (например, "MARKET", "LIMIT")
    private String timeInForce;    // Время действия ордера (например, "GTC" - Good 'Til Canceled)
    private BigDecimal price;      // Цена на которой исполнился ордер
    private BigDecimal quantity;   // Количество актива в ордере
    private BigDecimal executedQty; // Количество исполненных активов
    private BigDecimal cumulativeQuoteQuantity; // Кумулятивная стоимость сделки
    private Long time;             // Время, когда был создан ордер (в миллисекундах)
    private Long updateTime;       // Время обновления ордера (в миллисекундах)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_trade_id")
    private ActiveTrade activeTrade; // Связь с активной сделкой, к которой относится ордер

    public BinanceOrderResponse() {
        // Default constructor
    }

    public BinanceOrderResponse(String orderId, String symbol, String status, String side, String type, 
                                 String timeInForce, BigDecimal price, BigDecimal quantity, BigDecimal executedQty, 
                                 BigDecimal cumulativeQuoteQuantity, Long time, Long updateTime, ActiveTrade activeTrade) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.status = status;
        this.side = side;
        this.type = type;
        this.timeInForce = timeInForce;
        this.price = price;
        this.quantity = quantity;
        this.executedQty = executedQty;
        this.cumulativeQuoteQuantity = cumulativeQuoteQuantity;
        this.time = time;
        this.updateTime = updateTime;
        this.activeTrade = activeTrade;
    }
}
