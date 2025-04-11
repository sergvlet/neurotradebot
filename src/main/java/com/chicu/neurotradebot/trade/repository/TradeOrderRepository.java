package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
    TradeOrder findByOrderId(String orderId);
}
