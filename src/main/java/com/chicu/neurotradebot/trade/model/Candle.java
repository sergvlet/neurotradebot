// src/main/java/com/chicu/neurotradebot/trade/model/Candle.java
package com.chicu.neurotradebot.trade.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Одна свеча OHLCV.
 */
public class Candle {
    private Instant openTime;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;

    public Candle() {}

    public Candle(Instant openTime,
                  BigDecimal open,
                  BigDecimal high,
                  BigDecimal low,
                  BigDecimal close,
                  BigDecimal volume) {
        this.openTime = openTime;
        this.open     = open;
        this.high     = high;
        this.low      = low;
        this.close    = close;
        this.volume   = volume;
    }

    public Instant getOpenTime() { return openTime; }
    public void setOpenTime(Instant openTime) { this.openTime = openTime; }

    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }

    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }

    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }

    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }

    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
}
