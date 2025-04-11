package com.chicu.neurotradebot.trade.controller;

import com.chicu.neurotradebot.trade.model.TradeOrder;
import com.chicu.neurotradebot.trade.service.TradeOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade/order")
@RequiredArgsConstructor
public class TradeOrderController {

    private final TradeOrderService tradeOrderService;

    // Сохранение ордера
    @PostMapping("/save")
    public ResponseEntity<TradeOrder> saveTradeOrder(@RequestBody TradeOrder tradeOrder) {
        TradeOrder savedOrder = tradeOrderService.save(tradeOrder);
        return ResponseEntity.ok(savedOrder);
    }

    // Получение ордеров для активной сделки
    @GetMapping("/active/{activeTradeId}")
    public ResponseEntity<List<TradeOrder>> getOrdersByActiveTradeId(@PathVariable Long activeTradeId) {
        List<TradeOrder> orders = tradeOrderService.getOrdersByActiveTradeId(activeTradeId);
        return ResponseEntity.ok(orders);
    }
}
