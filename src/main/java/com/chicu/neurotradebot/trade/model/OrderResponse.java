// src/main/java/com/chicu/neurotradebot/trade/model/OrderResponse.java
package com.chicu.neurotradebot.trade.model;

import java.math.BigDecimal;

/**
 * Ответ на выставленный ордер.
 */
public class OrderResponse {
    public enum Side { BUY, SELL }

    private String orderId;
    private Side side;
    private BigDecimal executedQty;
    private BigDecimal price;

    public OrderResponse() {}

    public OrderResponse(String orderId,
                         Side side,
                         BigDecimal executedQty,
                         BigDecimal price) {
        this.orderId      = orderId;
        this.side         = side;
        this.executedQty  = executedQty;
        this.price        = price;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Side getSide() { return side; }
    public void setSide(Side side) { this.side = side; }

    public BigDecimal getExecutedQty() { return executedQty; }
    public void setExecutedQty(BigDecimal executedQty) { this.executedQty = executedQty; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
