// src/main/java/com/chicu/neurotradebot/service/RiskManagementService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;

public interface RiskManagementService {
    /** Проверить и применить риск-лимиты (drawdown, VaR и т.д.). */
    void enforceRiskLimits(AiTradeSettings settings);
}
