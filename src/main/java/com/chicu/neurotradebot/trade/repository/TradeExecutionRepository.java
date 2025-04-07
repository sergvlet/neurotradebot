package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeExecutionRepository extends JpaRepository<TradeExecution, Long> {
}