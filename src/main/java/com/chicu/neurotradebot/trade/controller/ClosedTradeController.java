package com.chicu.neurotradebot.trade.controller;

import com.chicu.neurotradebot.trade.model.ClosedTrade;
import com.chicu.neurotradebot.trade.service.ClosedTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade/closed")
@RequiredArgsConstructor
public class ClosedTradeController {

    private final ClosedTradeService closedTradeService;

    // Получение списка закрытых сделок по chatId
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<ClosedTrade>> getClosedTradesByChatId(@PathVariable Long chatId) {
        List<ClosedTrade> closedTrades = closedTradeService.getClosedTradesByChatId(chatId);
        return ResponseEntity.ok(closedTrades);
    }

    // Получение конкретной закрытой сделки по chatId и symbol
    @GetMapping("/chat/{chatId}/symbol/{symbol}")
    public ResponseEntity<ClosedTrade> getClosedTradeBySymbolAndChatId(@PathVariable Long chatId, @PathVariable String symbol) {
        ClosedTrade closedTrade = closedTradeService.getClosedTradeBySymbolAndChatId(symbol, chatId);
        return closedTrade != null ? ResponseEntity.ok(closedTrade) : ResponseEntity.notFound().build();
    }

    // Сохранение или обновление закрытой сделки
    @PostMapping("/save")
    public ResponseEntity<ClosedTrade> saveClosedTrade(@RequestBody ClosedTrade closedTrade) {
        ClosedTrade savedTrade = closedTradeService.save(closedTrade);
        return ResponseEntity.ok(savedTrade);
    }

    // Удаление всех закрытых сделок для chatId
    @DeleteMapping("/delete/chat/{chatId}")
    public ResponseEntity<Void> deleteClosedTradesByChatId(@PathVariable Long chatId) {
        closedTradeService.deleteClosedTradesByChatId(chatId);
        return ResponseEntity.noContent().build();
    }

    // Удаление конкретной закрытой сделки по chatId и symbol
    @DeleteMapping("/delete/chat/{chatId}/symbol/{symbol}")
    public ResponseEntity<Void> deleteClosedTradeBySymbolAndChatId(@PathVariable Long chatId, @PathVariable String symbol) {
        closedTradeService.deleteClosedTradeBySymbolAndChatId(symbol, chatId);
        return ResponseEntity.noContent().build();
    }
}
