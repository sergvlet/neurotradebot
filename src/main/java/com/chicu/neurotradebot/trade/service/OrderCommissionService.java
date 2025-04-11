package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.OrderCommission;
import com.chicu.neurotradebot.trade.repository.OrderCommissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCommissionService {

    private final OrderCommissionRepository orderCommissionRepository;

    // Найти все комиссии
    public List<OrderCommission> findAll() {
        return orderCommissionRepository.findAll();
    }

    // Найти комиссию по chatId и symbol
    public OrderCommission findByChatIdAndSymbol(Long chatId, String symbol) {
        return orderCommissionRepository.findByChatIdAndSymbol(chatId, symbol).orElse(null);
    }

    // Сохранить или обновить комиссию
    public OrderCommission save(OrderCommission orderCommission) {
        return orderCommissionRepository.save(orderCommission);
    }

    // Удалить все комиссии по chatId
    public void deleteByChatId(Long chatId) {
        orderCommissionRepository.deleteByChatId(chatId);
    }

    // Удалить комиссию по chatId и symbol
    public void deleteByChatIdAndSymbol(Long chatId, String symbol) {
        orderCommissionRepository.deleteByChatIdAndSymbol(chatId, symbol);
    }
}
