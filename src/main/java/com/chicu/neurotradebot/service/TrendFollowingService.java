// src/main/java/com/chicu/neurotradebot/service/TrendFollowingService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;

public interface TrendFollowingService {
    /** Применить логику trend-following и вернуть отчет. */
    String applyTrend(AiTradeSettings settings);
}
