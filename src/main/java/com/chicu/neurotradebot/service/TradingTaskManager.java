// src/main/java/com/chicu/neurotradebot/service/TradingTaskManager.java
package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.trade.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class TradingTaskManager {

    private final TaskScheduler            scheduler;
    private final TradingService           tradingService;
    private final AiTradeSettingsService   settingsService;

    /** Храним запланированные задачи по chatId */
    private final Map<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    /**
     * Запланировать или перезапланировать цикл для данного чата.
     */
    public void scheduleFor(Long chatId) {
        // отменяем старую, если есть
        cancelFor(chatId);

        // получаем настройки (в том числе scanInterval)
        var cfg = settingsService.getByChatId(chatId);
        Duration interval = cfg.getScanInterval();

        // планируем задачу с явной передачей chatId
        ScheduledFuture<?> f = scheduler.scheduleAtFixedRate(
                () -> tradingService.executeCycle(chatId),
                Instant.now(),
                interval
        );
        tasks.put(chatId, f);
    }

    /** Отменить таск, если он есть */
    public void cancelFor(Long chatId) {
        var old = tasks.remove(chatId);
        if (old != null) {
            old.cancel(false);
        }
    }
}
