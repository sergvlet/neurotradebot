// src/main/java/com/chicu/neurotradebot/trade/strategy/util/MacdResult.java
package com.chicu.neurotradebot.trade.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DTO для результатов расчёта MACD.
 */
@Getter
@AllArgsConstructor
public class MacdResult {
    private final BigDecimal macdLine;
    private final BigDecimal signalLine;
}
