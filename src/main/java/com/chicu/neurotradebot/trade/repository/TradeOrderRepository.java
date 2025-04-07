package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
}