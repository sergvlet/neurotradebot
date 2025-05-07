// src/main/java/com/chicu/neurotradebot/service/impl/RegimeDetectionServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.RegimeDetectionService;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class RegimeDetectionServiceImpl implements RegimeDetectionService {

    private final Random rnd = new Random();

    @Override
    public Regime predictCurrentRegime(AiTradeSettings settings) {
        // TODO: заменить на реальный inference CNN+LSTM
        // сейчас по рандому для теста
        int x = rnd.nextInt(3);
        return switch (x) {
            case 0 -> Regime.UPTREND;
            case 1 -> Regime.DOWNTREND;
            default -> Regime.FLAT;
        };
    }
}
