// src/main/java/com/chicu/neurotradebot/service/impl/RiskManagementServiceImpl.java
package com.chicu.neurotradebot.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.RiskManagementService;
import org.springframework.stereotype.Service;

@Service
public class RiskManagementServiceImpl implements RiskManagementService {

    @Override
    public void enforceRiskLimits(AiTradeSettings settings) {
        // TODO: проверка MaxDrawdown, VaR/CVaR, daily-stop
        // пока ничего не делает
    }
}
