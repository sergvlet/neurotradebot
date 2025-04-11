package com.chicu.neurotradebot.trade.controller;


import com.chicu.neurotradebot.trade.model.ActiveTrade;
import com.chicu.neurotradebot.trade.service.ActiveTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/trade/active")
@RequiredArgsConstructor
public class ActiveTradeController {

    private final ActiveTradeService activeTradeService;

    // Получение активной сделки по chatId и symbol
    @GetMapping("/active/{chatId}/{symbol}")
    public ResponseEntity<ActiveTrade> getActiveTrade(@PathVariable Long chatId, @PathVariable String symbol) {
        Optional<ActiveTrade> activeTrade = activeTradeService.getActive(chatId, symbol);
        return activeTrade.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Проверка существования активной сделки по chatId и symbol
    @GetMapping("/exists/{chatId}/{symbol}")
    public ResponseEntity<Boolean> existsActiveTrade(@PathVariable Long chatId, @PathVariable String symbol) {
        boolean exists = activeTradeService.existsActiveTrade(chatId, symbol);
        return ResponseEntity.ok(exists);
    }

    // Сохранение или обновление активной сделки
    @PostMapping("/save")
    public ResponseEntity<ActiveTrade> saveActiveTrade(@RequestBody ActiveTrade activeTrade) {
        ActiveTrade savedTrade = activeTradeService.save(activeTrade);
        return ResponseEntity.ok(savedTrade);
    }

    // Удаление всех активных сделок по chatId
    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<Void> deleteActiveTradesByChatId(@PathVariable Long chatId) {
        activeTradeService.deleteByChatId(chatId);
        return ResponseEntity.noContent().build();
    }

    // Удаление активной сделки по chatId и symbol
    @DeleteMapping("/delete/{chatId}/{symbol}")
    public ResponseEntity<Void> deleteActiveTrade(@PathVariable Long chatId, @PathVariable String symbol) {
        activeTradeService.deleteByChatIdAndSymbol(chatId, symbol);
        return ResponseEntity.noContent().build();
    }
}
