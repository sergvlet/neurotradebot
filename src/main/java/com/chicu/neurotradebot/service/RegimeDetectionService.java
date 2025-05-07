// src/main/java/com/chicu/neurotradebot/service/RegimeDetectionService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;

public interface RegimeDetectionService {
    enum Regime { UPTREND, DOWNTREND, FLAT }
    /** Собрать мульти-таймфреймные фичи и предсказать режим рынка. */
    Regime predictCurrentRegime(AiTradeSettings settings);;
}
