package com.chicu.neurotradebot.trade.controller;

import com.chicu.neurotradebot.trade.model.TradeLog;
import com.chicu.neurotradebot.trade.service.TradeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade/logs")
@RequiredArgsConstructor
public class TradeLogController {

    private final TradeLogService tradeLogService;

    // Получить все логи сделок
    @GetMapping
    public ResponseEntity<List<TradeLog>> getAllLogs() {
        List<TradeLog> tradeLogs = tradeLogService.getAllLogs();
        return ResponseEntity.ok(tradeLogs);
    }

    // Получить логи сделок для активной сделки
    @GetMapping("/active/{activeTradeId}")
    public ResponseEntity<List<TradeLog>> getLogsByActiveTrade(@PathVariable Long activeTradeId) {
        List<TradeLog> tradeLogs = tradeLogService.getLogsByActiveTradeId(activeTradeId);
        return ResponseEntity.ok(tradeLogs);
    }

    // Создать новый лог
    @PostMapping("/save")
    public ResponseEntity<TradeLog> createLog(@RequestBody TradeLog tradeLog) {
        TradeLog savedTradeLog = tradeLogService.save(tradeLog);
        return ResponseEntity.ok(savedTradeLog);
    }
}
