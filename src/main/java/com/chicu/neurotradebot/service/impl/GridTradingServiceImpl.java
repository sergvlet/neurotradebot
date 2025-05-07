// src/main/java/com/chicu/neurotradebot/service/impl/GridTradingServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.GridTradingService;
import org.springframework.stereotype.Service;

@Service
public class GridTradingServiceImpl implements GridTradingService {

    @Override
    public String applyGrid(AiTradeSettings settings) {
        // TODO: реализовать построение сетки, выставление ордеров
        return "▶️ GridTradingService: сетка выставлена (заглушка)";
    }
}
