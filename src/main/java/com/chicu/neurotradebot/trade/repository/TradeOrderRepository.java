package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
    // Получить ордера для конкретной активной сделки
    List<TradeOrder> findByActiveTradeId(Long activeTradeId);
}
