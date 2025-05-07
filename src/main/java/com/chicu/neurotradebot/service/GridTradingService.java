// src/main/java/com/chicu/neurotradebot/service/GridTradingService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;

public interface GridTradingService {
    /** Построить и выставить сетку ордеров, вернуть отчет. */
    String applyGrid(AiTradeSettings settings);
}
