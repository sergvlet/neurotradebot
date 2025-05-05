// src/main/java/com/chicu/neurotradebot/model/Bar.java
package com.chicu.neurotradebot.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Модель одного OHLCV-бара.
 */
@Data
@Builder
public class Bar {
    /** Время открытия в миллисекундах с эпохи */
    private long openTime;
    /** Цена открытия */
    private BigDecimal open;
    /** Максимальная цена */
    private BigDecimal high;
    /** Минимальная цена */
    private BigDecimal low;
    /** Цена закрытия */
    private BigDecimal close;
    /** Объём */
    private BigDecimal volume;
}
