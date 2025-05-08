// src/main/java/com/chicu/neurotradebot/trade/risk/RiskResult.java
package com.chicu.neurotradebot.trade.risk;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RiskResult {
    private final BigDecimal quantity;
    private final BigDecimal stopLossPrice;
    private final BigDecimal takeProfitPrice;

    public static RiskResult empty() {
        return new RiskResult(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
