// src/main/java/com/chicu/neurotradebot/trade/model/AccountBalance.java
package com.chicu.neurotradebot.trade.model;

import java.math.BigDecimal;

/**
 * Баланс по конкретному активу.
 */
public class AccountBalance {
    private String asset;
    private BigDecimal free;
    private BigDecimal locked;

    public AccountBalance() {}

    public AccountBalance(String asset,
                          BigDecimal free,
                          BigDecimal locked) {
        this.asset  = asset;
        this.free   = free;
        this.locked = locked;
    }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public BigDecimal getFree() { return free; }
    public void setFree(BigDecimal free) { this.free = free; }

    public BigDecimal getLocked() { return locked; }
    public void setLocked(BigDecimal locked) { this.locked = locked; }
}
