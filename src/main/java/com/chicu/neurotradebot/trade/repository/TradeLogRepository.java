package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {
}