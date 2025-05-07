// src/main/java/com/chicu/neurotradebot/service/impl/TrendFollowingServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.TrendFollowingService;
import org.springframework.stereotype.Service;

@Service
public class TrendFollowingServiceImpl implements TrendFollowingService {

    @Override
    public String applyTrend(AiTradeSettings settings) {
        // TODO: реализовать EMA-кросс + RSI-фильтр + SL/TP/трейлинг
        return "▶️ TrendFollowingService: тренд-стратегия выполнена (заглушка)";
    }
}
