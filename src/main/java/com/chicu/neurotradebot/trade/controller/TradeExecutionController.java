package com.chicu.neurotradebot.trade.controller;

import com.chicu.neurotradebot.trade.model.TradeExecution;
import com.chicu.neurotradebot.trade.service.TradeExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade-execution")
@RequiredArgsConstructor
public class TradeExecutionController {

    private final TradeExecutionService tradeExecutionService;

    // Получить все исполнения для chatId
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<TradeExecution>> getExecutionsByChatId(@PathVariable Long chatId) {
        List<TradeExecution> executions = tradeExecutionService.findByChatId(chatId);
        return executions.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(executions);
    }

    // Получить все исполнения для symbol
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<TradeExecution>> getExecutionsBySymbol(@PathVariable String symbol) {
        List<TradeExecution> executions = tradeExecutionService.findBySymbol(symbol);
        return executions.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(executions);
    }

    // Сохранить исполнение
    @PostMapping("/save")
    public ResponseEntity<TradeExecution> saveExecution(@RequestBody TradeExecution tradeExecution) {
        TradeExecution savedExecution = tradeExecutionService.save(tradeExecution);
        return ResponseEntity.ok(savedExecution);
    }

    // Удалить все исполнения для chatId
    @DeleteMapping("/delete/chat/{chatId}")
    public ResponseEntity<Void> deleteByChatId(@PathVariable Long chatId) {
        tradeExecutionService.deleteByChatId(chatId);
        return ResponseEntity.noContent().build();
    }

    // Удалить все исполнения для symbol
    @DeleteMapping("/delete/symbol/{symbol}")
    public ResponseEntity<Void> deleteBySymbol(@PathVariable String symbol) {
        tradeExecutionService.deleteBySymbol(symbol);
        return ResponseEntity.noContent().build();
    }
}
