// src/main/java/com/chicu/neurotradebot/service/AutoSetupService.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.RegimeDetectionService.Regime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AutoSetupService {

    private final AiTradeSettingsService     settingsService;
    private final RegimeDetectionService     regimeService;
    private final TrendFollowingService      trendService;
    private final GridTradingService         gridService;
    private final RiskManagementService      riskService;

    /**
     * Автонастройка для конкретного chatId.
     * @param chatId Telegram chatId пользователя.
     * @return текст-отчет о проделанной работе.
     */
    public String runAutoSetupForChat(Long chatId) {
        // 1) Загружаем настройки
        AiTradeSettings settings = settingsService.getByChatId(chatId);
        if (settings == null || !settings.isEnabled()) {
            return "⚠️ Автонастройка не запущена (enabled=false)";
        }

        StringBuilder report = new StringBuilder();

        // 2) Проверяем риски до начала
        riskService.enforceRiskLimits(settings);
        report.append("✅ Риски проверены\n");

        // 3) Детектируем режим
        Regime r = regimeService.predictCurrentRegime(settings);
        report.append("🔍 Режим: ").append(r).append("\n\n");

        // 4) Применяем стратегию
        String stratReport;
        if (r == Regime.UPTREND || r == Regime.DOWNTREND) {
            stratReport = trendService.applyTrend(settings);
        } else {
            stratReport = gridService.applyGrid(settings);
        }
        report.append(stratReport).append("\n\n");

        // 5) Проверяем риски после
        riskService.enforceRiskLimits(settings);
        report.append("✅ Риски после позиций проверены");

        return report.toString();
    }
}
