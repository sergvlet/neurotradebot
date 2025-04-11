package com.chicu.neurotradebot.trade.controller;

import com.chicu.neurotradebot.trade.model.OrderCommission;
import com.chicu.neurotradebot.trade.service.OrderCommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade/order-commission")
@RequiredArgsConstructor
public class OrderCommissionController {

    private final OrderCommissionService orderCommissionService;

    // Получение всех записей о комиссиях
    @GetMapping("/all")
    public ResponseEntity<List<OrderCommission>> getAllCommissions() {
        List<OrderCommission> commissions = orderCommissionService.findAll();
        return ResponseEntity.ok(commissions);
    }

    // Получение комиссии по chatId и symbol
    @GetMapping("/get/{chatId}/{symbol}")
    public ResponseEntity<OrderCommission> getCommissionByChatIdAndSymbol(@PathVariable Long chatId, @PathVariable String symbol) {
        OrderCommission commission = orderCommissionService.findByChatIdAndSymbol(chatId, symbol);
        return commission != null ? ResponseEntity.ok(commission) : ResponseEntity.notFound().build();
    }

    // Сохранение или обновление записи о комиссии
    @PostMapping("/save")
    public ResponseEntity<OrderCommission> saveCommission(@RequestBody OrderCommission orderCommission) {
        OrderCommission savedCommission = orderCommissionService.save(orderCommission);
        return ResponseEntity.ok(savedCommission);
    }

    // Удаление комиссии по chatId
    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<Void> deleteCommissionsByChatId(@PathVariable Long chatId) {
        orderCommissionService.deleteByChatId(chatId);
        return ResponseEntity.noContent().build();
    }

    // Удаление комиссии по chatId и symbol
    @DeleteMapping("/delete/{chatId}/{symbol}")
    public ResponseEntity<Void> deleteCommission(@PathVariable Long chatId, @PathVariable String symbol) {
        orderCommissionService.deleteByChatIdAndSymbol(chatId, symbol);
        return ResponseEntity.noContent().build();
    }
}
