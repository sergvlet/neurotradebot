package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.TradeOrder;
import com.chicu.neurotradebot.trade.repository.TradeOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeOrderService {

    private final TradeOrderRepository tradeOrderRepository;

    // Сохранить новый ордер
    public TradeOrder save(TradeOrder tradeOrder) {
        return tradeOrderRepository.save(tradeOrder);
    }

    // Получить все ордера для конкретной активной сделки
    public List<TradeOrder> getOrdersByActiveTradeId(Long activeTradeId) {
        return tradeOrderRepository.findByActiveTradeId(activeTradeId);
    }
}
