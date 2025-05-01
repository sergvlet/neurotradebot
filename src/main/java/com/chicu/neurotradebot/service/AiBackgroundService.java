package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.model.AiTradeSettings;
import com.chicu.neurotradebot.model.Candle;
import com.chicu.neurotradebot.model.StrategySignal;
import com.chicu.neurotradebot.repository.AiTradeSettingsRepository;
import com.chicu.neurotradebot.strategy.RsiEmaStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiBackgroundService {

    private final AiTradeSettingsRepository aiTradeSettingsRepository;
    private final BinanceCandleService binanceCandleService;
    private final RsiEmaStrategy rsiEmaStrategy;
    private final TelegramNotificationService telegramService;
    private final AiTradeSettingsService aiTradeSettingsService;

    @Scheduled(fixedDelay = 15000)
    public void runAiStrategies() {
        List<AiTradeSettings> sessions = aiTradeSettingsRepository.findByRunningTrue();

        for (AiTradeSettings settings : sessions) {
            try {
                aiTradeSettingsService.initializeDefaultsIfNull(settings);

                Long userId = settings.getUserTradingSettings().getUser().getId();
                boolean isTestnet = Boolean.TRUE.equals(settings.getUserTradingSettings().getUseTestnet());
                int minCandles = settings.getMinCandles();
                String strategyName = settings.getStrategy();

                List<String> pairsToCheck = new ArrayList<>();

                switch (settings.getPairMode()) {
                    case "MANUAL" -> {
                        if (settings.getManualPair() != null && !settings.getManualPair().isBlank()) {
                            pairsToCheck.add(settings.getManualPair().replace("/", ""));
                        }
                    }
                    case "LIST" -> {
                        if (settings.getAllowedPairs() != null) {
                            for (String line : settings.getAllowedPairs().split("\\n")) {
                                if (!line.isBlank()) {
                                    pairsToCheck.addAll(Arrays.stream(line.split(","))
                                            .map(String::trim)
                                            .filter(p -> !p.isBlank())
                                            .map(p -> p.replace("/", ""))
                                            .toList());
                                }
                            }
                        }
                    }
                    case "AUTO" -> {
                        // Пока временный список (в будущем — логика выбора на основе объёма, волатильности и т.д.)
                        pairsToCheck = List.of("BTCUSDT", "ETHUSDT", "BNBUSDT");
                    }
                }

                for (String symbol : pairsToCheck) {
                    try {
                        List<Candle> candles = binanceCandleService.getRecentCandles(symbol, "15m", minCandles + 1, isTestnet);
                        if (candles.size() < minCandles) {
                            log.warn("⏳ Недостаточно свечей для {} ({} < {})", symbol, candles.size(), minCandles);
                            continue;
                        }

                        StrategySignal signal = rsiEmaStrategy.evaluate(candles, settings);
                        if (signal != StrategySignal.NONE) {
                            log.info("🚨 AI сигнал для {}: {} по паре {}", userId, signal, symbol);
                            if (Boolean.TRUE.equals(settings.getNotifications())) {
                                String direction = signal == StrategySignal.BUY ? "📈 ПОКУПКА" : "📉 ПРОДАЖА";
                                String msg = """
                                ⚡ *AI-сигнал:* %s
                                Пара: *%s*
                                Стратегия: *%s*
                                Время: %s
                                """.formatted(direction, symbol, strategyName, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
                                telegramService.sendMessage(userId, msg);
                            }
                        }

                    } catch (Exception ex) {
                        log.error("❌ Ошибка обработки пары {} у пользователя {}: {}", symbol, userId, ex.getMessage(), ex);
                    }
                }

            } catch (Exception e) {
                Long userId = settings.getUserTradingSettings().getUser().getId();
                log.error("❌ Ошибка стратегии AI у пользователя {}: {}", userId, e.getMessage(), e);
            }
        }
    }

}
